package br.com.projeto.estudioBDD.dto;

import java.time.LocalDate;
import java.util.List;
import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;
import br.com.projeto.estudioBDD.database.model.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class HistoriaDto {
    private HistoriaDto() {}

    public record Request(
            @NotBlank(message = "O título é obrigatório") String titulo,
            @NotBlank(message = "O papel é obrigatório") String papel,
            @NotBlank(message = "O objetivo é obrigatório") String objetivo,
            @NotBlank(message = "O benefício é obrigatório") String beneficio,
            @NotNull(message = "A prioridade é obrigatória") Prioridade prioridade) {}

    public record Response(Long id, String titulo, String papel, String objetivo, String beneficio,
                           Prioridade prioridade, LocalDate dataCriacao, LocalDate dataAtualizacao,
                           Long projetoId, Long autorId, String autorNome,
                           List<CenarioDto.Response> cenarios) {
        public static Response from(HistoriaUsuario h) {
            return new Response(h.getId(), h.getTitulo(), h.getPapel(), h.getObjetivo(), h.getBeneficio(),
                    h.getPrioridade(), h.getDataCriacao(), h.getDataAtualizacao(),
                    h.getProjeto().getId(), h.getUsuario().getId(), h.getUsuario().getNome(),
                    h.getCenarios().stream().map(CenarioDto.Response::from).toList());
        }
    }
}
