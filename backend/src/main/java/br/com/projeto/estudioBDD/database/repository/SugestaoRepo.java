package br.com.projeto.estudioBDD.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.projeto.estudioBDD.database.model.Sugestao;
import br.com.projeto.estudioBDD.database.model.enums.*;

public interface SugestaoRepo extends JpaRepository<Sugestao, Long> {
List<Sugestao> findByHistoriaUsuarioIdOrderByIdAsc(Long historiaId);

    List<Sugestao> findByHistoriaUsuarioIdAndDecisaoOrderByIdAsc(Long historiaId, DecisaoSugestao decisao);

    boolean existsByHistoriaUsuarioIdAndDecisao(Long historiaId, DecisaoSugestao decisao);

    @Query("""
            select s.decisao, count(s) from Sugestao s
            where s.historiaUsuario.projeto.id = :projetoId
            group by s.decisao
            """)
    List<Object[]> contarPorDecisao(@Param("projetoId") Long projetoId);

    @Query("""
            select s.motivoRejeicao, count(s) from Sugestao s
            where s.historiaUsuario.projeto.id = :projetoId and s.motivoRejeicao is not null
            group by s.motivoRejeicao
            order by count(s) desc
            """)
    List<Object[]> contarMotivosRejeicao(@Param("projetoId") Long projetoId);
}

