package br.com.projeto.estudioBDD.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;
import br.com.projeto.estudioBDD.database.model.enums.Prioridade;
import br.com.projeto.estudioBDD.database.model.Projeto;
import br.com.projeto.estudioBDD.database.model.Usuario;
import br.com.projeto.estudioBDD.database.repository.HistoriaUsuarioRepo;
import br.com.projeto.estudioBDD.dto.HistoriaDto;
import br.com.projeto.estudioBDD.dto.HistoriaResumo;
import br.com.projeto.estudioBDD.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoriaService {

    private final HistoriaUsuarioRepo repository;
    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;

    @Transactional
    public HistoriaDto.Response criar(Long projetoId, Long usuarioId, HistoriaDto.Request req) {
        Projeto projeto = projetoService.obterEntidade(projetoId);
        Usuario autor = usuarioService.obterEntidade(usuarioId);
        String titulo = req.titulo().trim();

        if (repository.existsByProjetoIdAndTitulo(projetoId, titulo)) {
            throw RegraNegocioException.conflito("Já existe uma história com este título no projeto.");
        }

        HistoriaUsuario historia = repository.save(HistoriaUsuario.builder()
                .titulo(titulo)
                .papel(req.papel().trim())
                .objetivo(req.objetivo().trim())
                .beneficio(req.beneficio().trim())
                .prioridade(req.prioridade())
                .usuario(autor)
                .projeto(projeto)
                .build());
        return HistoriaDto.Response.from(historia);
    }

    /** Regra 6: lista com qtd de cenários e sugestões pendentes; filtro opcional por prioridade. */
    @Transactional(readOnly = true)
    public List<HistoriaResumo> listarPorProjeto(Long projetoId, Prioridade prioridade) {
        projetoService.obterEntidade(projetoId); // 404 se o projeto não existir
        List<Prioridade> prioridades = prioridade == null
                ? List.of(Prioridade.values())
                : List.of(prioridade);
        return repository.listarResumos(projetoId, prioridades);
    }

    @Transactional(readOnly = true)
    public HistoriaDto.Response buscar(Long id) {
        return HistoriaDto.Response.from(obterEntidade(id));
    }

    /** Regra 4: somente o autor edita. */
    @Transactional
    public HistoriaDto.Response atualizar(Long id, Long usuarioId, HistoriaDto.Request req) {
        HistoriaUsuario historia = obterParaEdicao(id, usuarioId);
        String titulo = req.titulo().trim();

        if (repository.existsByProjetoIdAndTituloAndIdNot(historia.getProjeto().getId(), titulo, id)) {
            throw RegraNegocioException.conflito("Já existe uma história com este título no projeto.");
        }

        historia.setTitulo(titulo);
        historia.setPapel(req.papel().trim());
        historia.setObjetivo(req.objetivo().trim());
        historia.setBeneficio(req.beneficio().trim());
        historia.setPrioridade(req.prioridade());
        return HistoriaDto.Response.from(historia);
    }

    /** Regra 4: somente o autor exclui (cenários e passos vão em cascata). */
    @Transactional
    public void excluir(Long id, Long usuarioId) {
        repository.delete(obterParaEdicao(id, usuarioId));
    }

    public HistoriaUsuario obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RegraNegocioException.naoEncontrado("História " + id + " não encontrada."));
    }

    /** Busca a história e garante que o usuário informado é o autor. */
    public HistoriaUsuario obterParaEdicao(Long id, Long usuarioId) {
        HistoriaUsuario historia = obterEntidade(id);
        if (!historia.getUsuario().getId().equals(usuarioId)) {
            throw RegraNegocioException.proibido("Somente o autor pode alterar ou excluir a história e seus cenários.");
        }
        return historia;
    }
}
