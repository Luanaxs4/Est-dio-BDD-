package br.com.projeto.estudioBDD.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.projeto.estudioBDD.database.model.enums.*;
import br.com.projeto.estudioBDD.database.model.Projeto;
import br.com.projeto.estudioBDD.database.repository.*;
import br.com.projeto.estudioBDD.dto.ProjetoDTO;
import br.com.projeto.estudioBDD.dto.ResumoProjeto;
import br.com.projeto.estudioBDD.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepo repository;
    private final HistoriaUsuarioRepo historiaRepository;
    private final CenarioRepo cenarioRepository;
    private final SugestaoRepo sugestaoRepository;

    @Transactional
    public ProjetoDTO.Response criar(ProjetoDTO.Request req) {
        String nome = req.nome().trim();
        if (repository.existsByNome(nome)) {
            throw RegraNegocioException.conflito("Já existe um projeto com este nome.");
        }
        Projeto projeto = repository.save(Projeto.builder()
                .nome(nome)
                .descricao(req.descricao())
                .contextoDominio(req.contextoDominio())
                .build());
        return ProjetoDTO.Response.from(projeto);
    }

    @Transactional(readOnly = true)
    public List<ProjetoDTO.Response> listar() {
        return repository.findAll().stream().map(ProjetoDTO.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjetoDTO.Response buscar(Long id) {
        return ProjetoDTO.Response.from(obterEntidade(id));
    }

    /** Regra 7: resumo do projeto. */
    @Transactional(readOnly = true)
    public ResumoProjeto resumo(Long id) {
        Projeto projeto = obterEntidade(id);

        Map<OrigemCenario, Long> porOrigem = new EnumMap<>(OrigemCenario.class);
        for (OrigemCenario o : OrigemCenario.values()) porOrigem.put(o, 0L);
        cenarioRepository.contarPorOrigem(id)
                .forEach(l -> porOrigem.put((OrigemCenario) l[0], (Long) l[1]));

        Map<DecisaoSugestao, Long> porDecisao = new EnumMap<>(DecisaoSugestao.class);
        for (DecisaoSugestao d : DecisaoSugestao.values()) porDecisao.put(d, 0L);
        sugestaoRepository.contarPorDecisao(id)
                .forEach(l -> porDecisao.put((DecisaoSugestao) l[0], (Long) l[1]));

        List<ResumoProjeto.MotivoContagem> motivos = sugestaoRepository.contarMotivosRejeicao(id).stream()
                .map(l -> new ResumoProjeto.MotivoContagem((MotivoRejeicao) l[0], (Long) l[1]))
                .toList();

        return new ResumoProjeto(projeto.getId(), projeto.getNome(),
                historiaRepository.countByProjetoId(id), porOrigem, porDecisao, motivos);
    }

    public Projeto obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RegraNegocioException.naoEncontrado("Projeto " + id + " não encontrado."));
    }
}
