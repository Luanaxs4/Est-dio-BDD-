package br.com.projeto.estudioBDD.dto;

import java.util.Map;
import br.com.projeto.estudioBDD.database.model.enums.*;


/** Resumo do projeto (regra 7). */
public record ResumoProjeto(Long projetoId, String projetoNome, long totalHistorias,
                            Map<OrigemCenario, Long> cenariosPorOrigem,
                            Map<DecisaoSugestao, Long> sugestoesPorDecisao,
                            Map<MotivoRejeicao, Long> motivosRejeicaoMaisFrequentes) {

    public record MotivoContagem(MotivoRejeicao motivo, long quantidade) {}
}
