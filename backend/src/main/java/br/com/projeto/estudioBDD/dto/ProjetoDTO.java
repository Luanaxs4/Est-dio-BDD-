package br.com.projeto.estudioBDD.dto;

import java.time.LocalDate;
import br.com.projeto.estudioBDD.database.model.Projeto;
import jakarta.validation.constraints.NotBlank;

public final class ProjetoDTO {
    private ProjetoDTO() {}

    public record Request(
            @NotBlank(message = "O nome é obrigatório") String nome,
            String descricao,
            String contextoDominio) {}

    public record Response(Long id, String nome, String descricao,
                           String contextoDominio, LocalDate dataCriacao) {
        public static Response from(Projeto p) {
            return new Response(p.getId(), p.getNome(), p.getDescricao(),
                    p.getContextoDominio(), p.getDataCriacao());
        }
    }
}
