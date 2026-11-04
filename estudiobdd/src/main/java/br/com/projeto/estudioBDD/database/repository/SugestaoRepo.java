package br.com.projeto.estudioBDD.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.estudioBDD.database.model.Sugestao;

public interface SugestaoRepo extends JpaRepository<Sugestao, Long> {

}
