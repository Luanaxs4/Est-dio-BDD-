package br.com.projeto.estudioBDD.dto;

import br.com.projeto.estudioBDD.database.model.enums.Prioridade;

/** Item da listagem de histórias de um projeto (regra 6). */
public record HistoriaResumo(Long id, String titulo, Prioridade prioridade,
                             Long qtdCenarios, Long qtdSugestoesPendentes) {}
