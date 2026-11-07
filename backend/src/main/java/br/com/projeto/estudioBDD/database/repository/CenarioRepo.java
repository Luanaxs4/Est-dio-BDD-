package br.com.projeto.estudioBDD.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.projeto.estudioBDD.database.model.Cenario;

public interface CenarioRepo extends JpaRepository<Cenario, Long> {
     boolean existsByHistoriaUsuarioIdAndTitulo(Long historiaId, String titulo);

    boolean existsByHistoriaUsuarioIdAndTituloAndIdNot(Long historiaId, String titulo, Long id);

    @Query("""
            select c.origem, count(c) from Cenario c
            where c.historiaUsuario.projeto.id = :projetoId
            group by c.origem
            """)
    List<Object[]> contarPorOrigem(@Param("projetoId") Long projetoId);
}
