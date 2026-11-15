package br.com.projeto.estudioBDD.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.projeto.estudioBDD.database.model.Cenario;
import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;
import br.com.projeto.estudioBDD.database.model.enums.*;
import br.com.projeto.estudioBDD.database.model.Passo;
import br.com.projeto.estudioBDD.database.repository.CenarioRepo;
import br.com.projeto.estudioBDD.dto.CenarioDto;
import br.com.projeto.estudioBDD.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CenarioService {

    private final CenarioRepo repository;
    private final HistoriaService historiaService;

    @Transactional(readOnly = true)
    public List<CenarioDto.Response> listar(Long historiaId) {
        return historiaService.obterEntidade(historiaId).getCenarios().stream()
                .map(CenarioDto.Response::from).toList();
    }

    @Transactional
    public CenarioDto.Response criar(Long historiaId, Long usuarioId, CenarioDto.Request req) {
        HistoriaUsuario historia = historiaService.obterParaEdicao(historiaId, usuarioId);
        String titulo = req.titulo().trim();

        if (repository.existsByHistoriaUsuarioIdAndTitulo(historiaId, titulo)) {
            throw RegraNegocioException.conflito("Já existe um cenário com este título na história.");
        }
        validarPassos(req.passos());

        Cenario cenario = Cenario.builder()
                .titulo(titulo)
                .origem(req.origem() == null ? OrigemCenario.HUMANO : req.origem())
                .posicao(historia.getCenarios().size() + 1)
                .historiaUsuario(historia)
                .build();
        montarPassos(cenario, req.passos());

        historia.getCenarios().add(cenario);
        historia.setDataAtualizacao(LocalDate.now());
        repository.save(cenario);
        return CenarioDto.Response.from(cenario);
    }

    @Transactional
    public CenarioDto.Response atualizar(Long cenarioId, Long usuarioId, CenarioDto.Request req) {
        Cenario cenario = obterEntidade(cenarioId);
        HistoriaUsuario historia = historiaService
                .obterParaEdicao(cenario.getHistoriaUsuario().getId(), usuarioId);
        String titulo = req.titulo().trim();

        if (repository.existsByHistoriaUsuarioIdAndTituloAndIdNot(historia.getId(), titulo, cenarioId)) {
            throw RegraNegocioException.conflito("Já existe um cenário com este título na história.");
        }
        validarPassos(req.passos());

        cenario.setTitulo(titulo);
        if (req.origem() != null) {
            cenario.setOrigem(req.origem()); // ex.: marcar SUGESTAO_IA depois de aceitar uma sugestão
        }
        cenario.getPassos().clear();
        montarPassos(cenario, req.passos());
        historia.setDataAtualizacao(LocalDate.now());
        repository.save(cenario); 
        return CenarioDto.Response.from(cenario);
    }

    @Transactional
    public void excluir(Long cenarioId, Long usuarioId) {
        Cenario cenario = obterEntidade(cenarioId);
        HistoriaUsuario historia = historiaService
                .obterParaEdicao(cenario.getHistoriaUsuario().getId(), usuarioId);

        historia.getCenarios().removeIf(c -> c.getId().equals(cenarioId));
        int posicao = 1;
        for (Cenario c : historia.getCenarios()) {
            c.setPosicao(posicao++); // mantém posições 1..n sem buracos
        }
        historia.setDataAtualizacao(LocalDate.now());
    }

    private Cenario obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RegraNegocioException.naoEncontrado("Cenário " + id + " não encontrado."));
    }

    private void montarPassos(Cenario cenario, List<CenarioDto.PassoRequest> passos) {
        for (int i = 0; i < passos.size(); i++) {
            CenarioDto.PassoRequest p = passos.get(i);
            cenario.getPassos().add(Passo.builder()
                    .tipo(p.tipo())
                    .texto(p.texto().trim())
                    .posicao(i + 1)
                    .cenario(cenario)
                    .build());
        }
    }

    /**
     * Regra 2: ao menos um QUANDO e um ENTAO; ordem DADO -> QUANDO -> ENTAO;
     * E e MAS continuam o tipo anterior e não podem iniciar o cenário.
     */
    private void validarPassos(List<CenarioDto.PassoRequest> passos) {
        TipoPasso faseAtual = null;
        boolean temQuando = false;
        boolean temEntao = false;

        for (int i = 0; i < passos.size(); i++) {
            TipoPasso tipo = passos.get(i).tipo();
            TipoPasso efetivo = tipo;

            if (tipo == TipoPasso.E || tipo == TipoPasso.MAS) {
                if (faseAtual == null) {
                    throw RegraNegocioException.regra("Os passos E e MAS não podem iniciar o cenário (passo " + (i + 1) + ").");
                }
                efetivo = faseAtual;
            }

            if (faseAtual != null && ordem(efetivo) < ordem(faseAtual)) {
                throw RegraNegocioException.regra("Ordem inválida no passo " + (i + 1)
                        + ": a sequência deve ser Dado, Quando, Então.");
            }
            faseAtual = efetivo;

            if (tipo == TipoPasso.QUANDO) temQuando = true;
            if (tipo == TipoPasso.ENTAO) temEntao = true;
        }

        if (!temQuando) throw RegraNegocioException.regra("O cenário precisa de ao menos um passo Quando.");
        if (!temEntao) throw RegraNegocioException.regra("O cenário precisa de ao menos um passo Então.");
    }

    private int ordem(TipoPasso tipo) {
        return switch (tipo) {
            case DADO -> 0;
            case QUANDO -> 1;
            case ENTAO -> 2;
            default -> throw new IllegalArgumentException("Tipo sem ordem própria: " + tipo);
        };
    }
}
