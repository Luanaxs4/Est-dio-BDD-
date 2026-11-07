package br.com.projeto.estudioBDD.database.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;
import br.com.projeto.estudioBDD.database.model.enums.*;
import br.com.projeto.estudioBDD.dto.HistoriaResumo;

public interface HistoriaUsuarioRepo extends JpaRepository<HistoriaUsuario, Long> {
    boolean existsByProjetoIdAndTitulo(Long projetoId, String titulo);

    boolean existsByProjetoIdAndTituloAndIdNot(Long projetoId, String titulo, Long id);

    long countByProjetoId(Long projetoId);

    @Query("""
            select new br.com.projeto.estudioBDD.dto.HistoriaResumo(
                h.id, h.titulo, h.prioridade,
                (select count(c) from Cenario c where c.historiaUsuario = h),
                (select count(s) from Sugestao s
                    where s.historiaUsuario = h and s.decisao = :pendente))
            from HistoriaUsuario h
            where h.projeto.id = :projetoId and h.prioridade in :prioridades
            order by h.id
            """)
    List<HistoriaResumo> buscarResumos(@Param("projetoId") Long projetoId,
                                       @Param("prioridades") Collection<Prioridade> prioridades,
                                       @Param("pendente") DecisaoSugestao pendente);

    default List<HistoriaResumo> listarResumos(Long projetoId, Collection<Prioridade> prioridades) {
        return buscarResumos(projetoId, prioridades, DecisaoSugestao.PENDENTE);
    }
}
