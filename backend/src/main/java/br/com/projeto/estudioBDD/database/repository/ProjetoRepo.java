package br.com.projeto.estudioBDD.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.estudioBDD.database.model.Projeto;

public interface ProjetoRepo extends JpaRepository<Projeto, Long> {
    boolean existsByNome(String nome);

}
