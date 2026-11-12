package br.com.projeto.estudioBDD.dto;

import java.util.List;
import br.com.projeto.estudioBDD.database.model.Cenario;
import br.com.projeto.estudioBDD.database.model.enums.*;
import br.com.projeto.estudioBDD.database.model.Passo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public final class CenarioDto {
    private CenarioDto() {}

    public record PassoRequest(
            @NotNull(message = "O tipo do passo é obrigatório") TipoPasso tipo,
            @NotBlank(message = "O texto do passo é obrigatório") String texto) {}

    /** origem é opcional: se não vier, assume HUMANO. */
    public record Request(
            @NotBlank(message = "O título é obrigatório") String titulo,
            @NotNull(message = "A posição é obrigatória") int posicao,
            OrigemCenario origem,
            @NotEmpty(message = "O cenário precisa ter passos") List<@Valid PassoRequest> passos) {}

    public record PassoResponse(Long id, TipoPasso tipo, String texto, int posicao) {
        public static PassoResponse from(Passo p) {
            return new PassoResponse(p.getId(), p.getTipo(), p.getTexto(), p.getPosicao());
        }
    }

    public record Response(Long id, String titulo, int posicao, OrigemCenario origem,
                           List<PassoResponse> passos) {
        public static Response from(Cenario c) {
            return new Response(c.getId(), c.getTitulo(), c.getPosicao(), c.getOrigem(),
                    c.getPassos().stream().map(PassoResponse::from).toList());
        }
    }
}
