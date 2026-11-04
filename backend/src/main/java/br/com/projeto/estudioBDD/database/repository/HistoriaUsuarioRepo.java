package br.com.projeto.estudioBDD.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;

public interface HistoriaUsuarioRepo extends JpaRepository<HistoriaUsuario, Long> {

}
