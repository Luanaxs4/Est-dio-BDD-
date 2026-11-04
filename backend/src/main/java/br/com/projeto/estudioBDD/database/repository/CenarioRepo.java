package br.com.projeto.estudioBDD.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.estudioBDD.database.model.Cenario;

public interface CenarioRepo extends JpaRepository<Cenario, Long> {

}
