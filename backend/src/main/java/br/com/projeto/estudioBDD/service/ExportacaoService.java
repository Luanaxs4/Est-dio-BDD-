package br.com.projeto.estudioBDD.service;

import java.text.Normalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.projeto.estudioBDD.database.model.Cenario;
import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;
import br.com.projeto.estudioBDD.database.model.Passo;
import br.com.projeto.estudioBDD.database.model.enums.DecisaoSugestao;
import br.com.projeto.estudioBDD.database.model.enums.TipoPasso;
import br.com.projeto.estudioBDD.database.repository.SugestaoRepo;
import br.com.projeto.estudioBDD.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExportacaoService {

    private final HistoriaService historiaService;
    private final SugestaoRepo sugestaoRepository;

    public record Arquivo(String nome, String conteudo) {}

    /** Regra 5: só exporta histórias com ao menos um cenário e sem sugestões pendentes. */
    @Transactional(readOnly = true)
    public Arquivo exportar(Long historiaId) {
        HistoriaUsuario historia = historiaService.obterEntidade(historiaId);

        if (historia.getCenarios().isEmpty()) {
            throw RegraNegocioException.regra("A história precisa ter ao menos um cenário para ser exportada.");
        }
        if (sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(historiaId, DecisaoSugestao.PENDENTE)) {
            throw RegraNegocioException.regra("A história possui sugestões pendentes. Decida todas antes de exportar.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("# language: pt\n");
        sb.append("Funcionalidade: ").append(historia.getTitulo().trim()).append("\n");
        sb.append("  ").append(prefixar("Como", historia.getPapel())).append("\n");
        sb.append("  ").append(prefixar("Quero", historia.getObjetivo())).append("\n");
        sb.append("  ").append(prefixar("Para", historia.getBeneficio())).append("\n");

        for (Cenario cenario : historia.getCenarios()) {
            sb.append("\n  Cenário: ").append(cenario.getTitulo().trim()).append("\n");
            for (Passo passo : cenario.getPassos()) {
                sb.append("    ").append(palavraChave(passo.getTipo()))
                  .append(" ").append(passo.getTexto().trim()).append("\n");
            }
        }
        return new Arquivo(nomeArquivo(historia), sb.toString());
    }

    private String palavraChave(TipoPasso tipo) {
        return switch (tipo) {
            case DADO -> "Dado";
            case QUANDO -> "Quando";
            case ENTAO -> "Então";
            case E -> "E";
            case MAS -> "Mas";
        };
    }

    /** Evita "Como Como analista" caso o usuário já tenha digitado o prefixo. */
    private String prefixar(String palavra, String texto) {
        String t = texto.trim();
        return t.regionMatches(true, 0, palavra + " ", 0, palavra.length() + 1) ? t : palavra + " " + t;
    }

    private String nomeArquivo(HistoriaUsuario historia) {
        String slug = Normalizer.normalize(historia.getTitulo(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (slug.isBlank()) slug = "historia-" + historia.getId();
        return slug + ".feature";
    }
}
