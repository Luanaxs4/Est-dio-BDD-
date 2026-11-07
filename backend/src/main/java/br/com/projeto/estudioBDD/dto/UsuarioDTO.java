package br.com.projeto.estudioBDD.dto;

import br.com.projeto.estudioBDD.database.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class UsuarioDTO {
    private UsuarioDTO() {}

    public record Request(
            @NotBlank(message = "O nome é obrigatório")
            String nome,
            @NotBlank(message = "O e-mail é obrigatório") 
            @Email(message = "E-mail inválido") 
            String email) {}

    public record Response(Long id, String nome, String email) {
        public static Response from(Usuario u) {
            return new Response(u.getId(), u.getNome(), u.getEmail());
        }
    }
}
