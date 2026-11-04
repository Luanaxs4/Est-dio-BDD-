package br.com.projeto.estudioBDD.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.estudioBDD.database.model.Usuario;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

}
