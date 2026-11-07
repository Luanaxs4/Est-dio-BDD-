package br.com.projeto.estudioBDD.dto;

import java.time.LocalDate;
import br.com.projeto.estudioBDD.database.model.enums.*;
import br.com.projeto.estudioBDD.database.model.Sugestao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class SugestaoDto {
    private SugestaoDto() {}

    public record Request(
            @NotBlank(message = "A ferramenta é obrigatória") String ferramenta,
            String promptUtilizado,
            @NotBlank(message = "O conteúdo é obrigatório") String conteudo) {}

    public record DecisaoRequest(
            @NotNull(message = "A decisão é obrigatória") DecisaoSugestao decisao,
            MotivoRejeicao motivoRejeicao,
            String justificativa) {}

    public record Response(Long id, String ferramenta, String promptUtilizado, String conteudo,
                           DecisaoSugestao decisao, MotivoRejeicao motivoRejeicao, String justificativa,
                           LocalDate dataRegistro, LocalDate dataDecisao,
                           Long historiaId, Long registradaPorId, Long decididaPorId) {
        public static Response from(Sugestao s) {
            return new Response(s.getId(), s.getFerramenta(), s.getPromptUtilizado(), s.getConteudo(),
                    s.getDecisao(), s.getMotivoRejeicao(), s.getJustificativa(),
                    s.getDataRegistro(), s.getDataDecisao(),
                    s.getHistoriaUsuario().getId(), s.getRegistradaPor().getId(),
                    s.getDecididaPor() == null ? null : s.getDecididaPor().getId());
        }
    }
}
