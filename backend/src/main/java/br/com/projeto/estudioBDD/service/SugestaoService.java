package br.com.projeto.estudioBDD.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.projeto.estudioBDD.database.model.enums.DecisaoSugestao;
import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;
import br.com.projeto.estudioBDD.database.model.Sugestao;
import br.com.projeto.estudioBDD.database.model.Usuario;
import br.com.projeto.estudioBDD.database.repository.SugestaoRepo;
import br.com.projeto.estudioBDD.dto.SugestaoDto;
import br.com.projeto.estudioBDD.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SugestaoService {

    private final SugestaoRepo repository;
    private final HistoriaService historiaService;
    private final UsuarioService usuarioService;

    /** Regra 4: sugestões começam PENDENTES. */
    @Transactional
    public SugestaoDto.Response registrar(Long historiaId, Long usuarioId, SugestaoDto.Request req) {
        HistoriaUsuario historia = historiaService.obterEntidade(historiaId);
        Usuario usuario = usuarioService.obterEntidade(usuarioId);

        Sugestao sugestao = repository.save(Sugestao.builder()
                .ferramenta(req.ferramenta().trim())
                .promptUtilizado(req.promptUtilizado())
                .conteudo(req.conteudo().trim())
                .decisao(DecisaoSugestao.PENDENTE)
                .historiaUsuario(historia)
                .registradaPor(usuario)
                .build());
        return SugestaoDto.Response.from(sugestao);
    }

    @Transactional(readOnly = true)
    public List<SugestaoDto.Response> listar(Long historiaId, DecisaoSugestao decisao) {
        historiaService.obterEntidade(historiaId); // 404 se a história não existir
        List<Sugestao> sugestoes = decisao == null
                ? repository.findByHistoriaUsuarioIdOrderByIdAsc(historiaId)
                : repository.findByHistoriaUsuarioIdAndDecisaoOrderByIdAsc(historiaId, decisao);
        return sugestoes.stream().map(SugestaoDto.Response::from).toList();
    }

    /**
     * Regra 4: a rejeição exige motivo; sugestão já decidida não pode ser alterada.
     * Aceitar NÃO altera os cenários: o analista faz o ajuste pelo endpoint de cenários.
     */
    @Transactional
    public SugestaoDto.Response decidir(Long sugestaoId, Long usuarioId, SugestaoDto.DecisaoRequest req) {
        Sugestao sugestao = repository.findById(sugestaoId)
                .orElseThrow(() -> RegraNegocioException.naoEncontrado("Sugestão " + sugestaoId + " não encontrada."));
        Usuario usuario = usuarioService.obterEntidade(usuarioId);

        if (sugestao.getDecisao() != DecisaoSugestao.PENDENTE) {
            throw RegraNegocioException.conflito("Uma sugestão já decidida não pode ser alterada.");
        }
        if (req.decisao() == DecisaoSugestao.PENDENTE) {
            throw RegraNegocioException.regra("A decisão deve ser ACEITA ou REJEITADA.");
        }
        if (req.decisao() == DecisaoSugestao.REJEITADA && req.motivoRejeicao() == null) {
            throw RegraNegocioException.regra("A rejeição exige um motivo.");
        }

        sugestao.setDecisao(req.decisao());
        sugestao.setMotivoRejeicao(req.decisao() == DecisaoSugestao.REJEITADA ? req.motivoRejeicao() : null);
        sugestao.setJustificativa(req.justificativa());
        sugestao.setDataDecisao(LocalDate.now());
        sugestao.setDecididaPor(usuario);
        return SugestaoDto.Response.from(sugestao);
    }
}
