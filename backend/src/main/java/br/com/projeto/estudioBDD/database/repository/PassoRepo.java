package br.com.projeto.estudioBDD.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.estudioBDD.database.model.Passo;

public interface PassoRepo extends JpaRepository<Passo, Long> {

}
