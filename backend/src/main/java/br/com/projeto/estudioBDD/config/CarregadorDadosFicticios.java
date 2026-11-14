package br.com.projeto.estudioBDD.config;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import br.com.projeto.estudioBDD.database.model.enums.*;
import br.com.projeto.estudioBDD.dto.*;
import br.com.projeto.estudioBDD.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Carrega dados fictícios ao iniciar a aplicação.
 * Só roda se application.properties tiver:  app.seed.ativo=true
 * Cria os 4 usuários e os 2 projetos se ainda não existirem.
 * Não duplica: se o projeto já tem histórias, ele é ignorado.
 * Usa apenas os serviços (nunca repositories), respeitando a regra do ArchUnit.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.ativo", havingValue = "true")
public class CarregadorDadosFicticios implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final ProjetoService projetoService;
    private final HistoriaService historiaService;
    private final CenarioService cenarioService;
    private final SugestaoService sugestaoService;

    @Override
    public void run(String... args) {
        try {
            List<Long> u = garantirUsuarios();

            Long bdd = garantirProjeto("estudio bdd", "Estúdio BDD",
                    "Documentação de requisitos com histórias de usuário e cenários BDD",
                    "História: Como/Quero/Para. Cenário: Dado/Quando/Então. Sugestão: proposta de IA aceita ou rejeitada.");
            Long croche = garantirProjeto("croche", "Estoque de Crochê",
                    "Controle de novelos e materiais usados em peças de crochê",
                    "Novelo: unidade de lã ou linha. Peça: item feito de crochê. Estoque mínimo: limite para alerta.");

            if (historiaService.listarPorProjeto(bdd, null).isEmpty()) {
                carregarEstudioBdd(bdd, u);
                log.info("Dados fictícios carregados no projeto Estúdio BDD.");
            } else {
                log.info("Projeto Estúdio BDD já tem histórias; carga ignorada.");
            }

            if (historiaService.listarPorProjeto(croche, null).isEmpty()) {
                carregarCroche(croche, u);
                log.info("Dados fictícios carregados no projeto Estoque de Crochê.");
            } else {
                log.info("Projeto Estoque de Crochê já tem histórias; carga ignorada.");
            }
        } catch (Exception e) {
            // não derruba a aplicação se a carga falhar
            log.error("Falha ao carregar dados fictícios: {}", e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // Usuários e projetos (cria se ainda não existirem)
    // ------------------------------------------------------------------

    /** Cria os 4 usuários fixos (se o e-mail já existir, reaproveita) e devolve os ids na ordem. */
    private List<Long> garantirUsuarios() {
        String[][] padrao = {
                {"Ana Analista", "ana@exemplo.com"},
                {"Bruno Barros", "bruno@exemplo.com"},
                {"Carla Costa", "carla@exemplo.com"},
                {"Diego Dias", "diego@exemplo.com"}};
        List<UsuarioDTO.Response> existentes = usuarioService.listar();
        List<Long> ids = new ArrayList<>();
        for (String[] dados : padrao) {
            Long id = existentes.stream()
                    .filter(x -> x.email().equalsIgnoreCase(dados[1]))
                    .map(UsuarioDTO.Response::id)
                    .findFirst()
                    .orElseGet(() -> usuarioService.criar(new UsuarioDTO.Request(dados[0], dados[1])).id());
            ids.add(id);
        }
        return ids;
    }

    /** Procura o projeto pelo nome (sem acento/maiúsculas); se não achar, cria. */
    private Long garantirProjeto(String chave, String nome, String descricao, String contexto) {
        return projetoService.listar().stream()
                .filter(p -> normalizar(p.nome()).contains(chave))
                .map(ProjetoDTO.Response::id)
                .findFirst()
                .orElseGet(() -> projetoService.criar(new ProjetoDTO.Request(nome, descricao, contexto)).id());
    }

    private String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }

    // ------------------------------------------------------------------
    // Estúdio BDD
    // ------------------------------------------------------------------

    private void carregarEstudioBdd(Long p, List<Long> u) {
        Long u1 = u.get(0), u2 = u.get(1), u3 = u.get(2), u4 = u.get(3);

        // História 1: Cadastro de cenários (ALTA)
        Long h1 = historia(p, u1, "Cadastro de cenários", "analista",
                "registrar os cenários de uma história", "documentar seus critérios de aceite", Prioridade.ALTA);
        cenario(h1, u1, "Cenário com Dado, Quando e Então é aceito", null,
                dado("a história \"Exportar feature\" de \"Ana\""),
                quando("Ana cadastra o cenário \"Exportação com sucesso\" com passos Dado, Quando e Então"),
                entao("o cenário é salvo na posição 1"),
                e("o cenário recebe a origem HUMANO"));
        cenario(h1, u1, "Cenário sem passo Então é rejeitado", null,
                dado("a história \"Exportar feature\" de \"Ana\""),
                quando("Ana cadastra um cenário só com Dado e Quando"),
                entao("o cadastro é rejeitado"),
                e("a mensagem informa que falta o passo Então"));
        cenario(h1, u1, "Cenário com título repetido é rejeitado", null,
                dado("a história \"Exportar feature\" com o cenário \"Exportação com sucesso\""),
                quando("Ana cadastra outro cenário chamado \"Exportação com sucesso\" na mesma história"),
                entao("o cadastro é rejeitado"),
                e("a mensagem informa título repetido"));
        Long s1 = sugestao(h1, u1, "ChatGPT",
                "Amplie os cenários da história Cadastro de cenários com casos de borda",
                "Cenário: Cenário iniciado por Mas é rejeitado. Dado a história \"Exportar feature\" de \"Ana\", Quando Ana cadastra um cenário cujo primeiro passo é Mas, Então o cadastro é rejeitado.");
        aceitar(s1, u1, "Caso de borda que faltava.");
        cenario(h1, u1, "Cenário iniciado por Mas é rejeitado", OrigemCenario.SUGESTAO_IA,
                dado("a história \"Exportar feature\" de \"Ana\""),
                quando("Ana cadastra um cenário cujo primeiro passo é Mas"),
                entao("o cadastro é rejeitado"),
                e("a mensagem informa que E e Mas não podem iniciar o cenário"));
        Long s2 = sugestao(h1, u2, "Claude",
                "Amplie os cenários da história Cadastro de cenários com casos de borda",
                "Dado que o sistema está funcionando corretamente");
        rejeitar(s2, u1, MotivoRejeicao.GENERICA, "Não descreve um contexto verificável.");
        Long s3 = sugestao(h1, u3, "Gemini",
                "Quais cenários de falha faltam no cadastro de cenários?",
                "Cenário: Cenário sem Então não é permitido. Dado um cenário com Dado e Quando, Então o sistema recusa o cadastro.");
        rejeitar(s3, u1, MotivoRejeicao.DUPLICADA, "Mesmo caso do cenário 'Cenário sem passo Então é rejeitado'.");

        // História 2: Registro de sugestões de IA (ALTA)
        Long h2 = historia(p, u2, "Registro de sugestões de IA", "analista",
                "registrar e decidir as sugestões obtidas com ferramentas de IA",
                "manter um histórico das decisões tomadas", Prioridade.ALTA);
        cenario(h2, u2, "Sugestão registrada começa pendente", null,
                dado("a história \"Login\" do projeto \"Loja\""),
                quando("o analista registra uma sugestão do \"ChatGPT\" para essa história"),
                entao("a sugestão fica com decisão PENDENTE"));
        cenario(h2, u2, "Rejeição sem motivo é recusada", null,
                dado("uma sugestão pendente na história \"Login\""),
                quando("o analista rejeita a sugestão sem informar motivo"),
                entao("a rejeição é recusada"),
                e("a sugestão continua pendente"));
        cenario(h2, u2, "Sugestão já decidida não pode ser alterada", null,
                dado("uma sugestão da história \"Login\" já aceita"),
                quando("o analista tenta rejeitar essa mesma sugestão"),
                entao("a alteração é recusada"),
                e("a decisão continua ACEITA"));
        cenario(h2, u2, "Aceitar sugestão não altera os cenários", null,
                dado("a história \"Login\" com 2 cenários e uma sugestão pendente"),
                quando("o analista aceita a sugestão"),
                entao("a decisão da sugestão passa a ACEITA"),
                e("a história continua com 2 cenários"));
        Long s4 = sugestao(h2, u2, "ChatGPT",
                "Sugira cenários para o registro de sugestões de IA",
                "Então os cenários são alterados ao aceitar a sugestão");
        rejeitar(s4, u2, MotivoRejeicao.INCORRETA, "Contradiz a regra 4: aceitar não altera os cenários.");
        Long s5 = sugestao(h2, u3, "Claude",
                "Sugira cenários para o registro de sugestões de IA",
                "Então o autor recebe um e-mail de confirmação");
        rejeitar(s5, u2, MotivoRejeicao.FORA_DO_ESCOPO, "O sistema não envia e-mails.");
        sugestao(h2, u4, "Gemini",
                "Quais cenários faltam no registro de sugestões?",
                "Cenário: Decisão registra quem decidiu. Dado uma sugestão pendente, Quando o analista \"Bruno\" aceita a sugestão, Então o sistema registra \"Bruno\" como quem decidiu."); // fica PENDENTE

        // História 3: Exportação do arquivo .feature (MEDIA)
        Long h3 = historia(p, u3, "Exportação do arquivo .feature", "analista",
                "exportar uma história em arquivo .feature",
                "usar os cenários em ferramentas de automação", Prioridade.MEDIA);
        cenario(h3, u3, "Exportação com cenário e sem pendências", null,
                dado("a história \"Login\" com 1 cenário e nenhuma sugestão pendente"),
                quando("o analista exporta a história"),
                entao("o arquivo login.feature é gerado"),
                e("o arquivo começa com \"# language: pt\""));
        cenario(h3, u3, "Exportação bloqueada com sugestão pendente", null,
                dado("a história \"Login\" com 1 cenário e 1 sugestão pendente"),
                quando("o analista exporta a história"),
                entao("a exportação é bloqueada"),
                e("a mensagem informa que há sugestões pendentes"));
        cenario(h3, u3, "Exportação bloqueada sem cenário", null,
                dado("a história \"Login\" sem nenhum cenário"),
                quando("o analista exporta a história"),
                entao("a exportação é bloqueada"),
                e("a mensagem informa que falta ao menos um cenário"));
        sugestao(h3, u3, "Claude", "Sugira cenários para a exportação do .feature",
                "Cenário: Exportação preserva a ordem dos cenários. Dado uma história com os cenários \"A\" e \"B\" nessa ordem, Quando o analista exporta, Então o arquivo traz \"A\" antes de \"B\"."); // PENDENTE
        sugestao(h3, u1, "ChatGPT", "Sugira cenários para a exportação do .feature",
                "Cenário: Nome do arquivo vem do título. Dado a história \"Login de cliente\", Quando o analista exporta, Então o arquivo se chama login-de-cliente.feature."); // PENDENTE

        // História 4: Consulta de projeto (MEDIA), regra 6
        Long h4 = historia(p, u1, "Consulta de projeto", "analista",
                "consultar o projeto", "visualizar suas informações", Prioridade.MEDIA);
        cenario(h4, u1, "Listagem concluída com sucesso", null,
                dado("o contexto de consulta de dados"),
                quando("consulto o projeto"),
                entao("visualizo a lista de histórias com a quantidade de cenários e sugestões pendentes"),
                e("a opção de filtro por prioridade é exibida na interface"));
        cenario(h4, u1, "Consulta realizada sem resultados", null,
                dado("o contexto de consulta"),
                quando("consulto um projeto que não possui histórias cadastradas"),
                entao("visualizo uma mensagem informando que não há histórias disponíveis para consulta"));
        cenario(h4, u1, "Filtragem de histórias por prioridade", null,
                dado("o contexto de consulta"),
                e("existem histórias cadastradas com diferentes prioridades"),
                quando("seleciono uma prioridade para filtrar as histórias"),
                entao("visualizo apenas as histórias que correspondem à prioridade selecionada, com suas respectivas quantidades de cenários e sugestões pendentes"));

        // História 5: Resumo do projeto (BAIXA)
        Long h5 = historia(p, u2, "Resumo do projeto", "analista",
                "consultar o resumo do projeto", "acompanhar o andamento da documentação", Prioridade.BAIXA);
        cenario(h5, u2, "Resumo mostra cenários por origem", null,
                dado("o projeto \"Estúdio BDD\" com 2 cenários HUMANO e 1 cenário SUGESTAO_IA"),
                quando("o analista consulta o resumo do projeto"),
                entao("o resumo mostra 2 cenários de origem HUMANO"),
                e("o resumo mostra 1 cenário de origem SUGESTAO_IA"));
        cenario(h5, u2, "Resumo ordena os motivos de rejeição mais frequentes", null,
                dado("2 sugestões rejeitadas por GENERICA e 1 por DUPLICADA"),
                quando("o analista consulta o resumo do projeto"),
                entao("o motivo GENERICA aparece antes de DUPLICADA"));
    }

    // ------------------------------------------------------------------
    // Estoque de Crochê
    // ------------------------------------------------------------------

    private void carregarCroche(Long p, List<Long> u) {
        Long u3 = u.get(2), u4 = u.get(3);

        Long h6 = historia(p, u4, "Cadastro de novelo", "artesã",
                "cadastrar os novelos de lã que tenho", "saber o que há disponível no estoque", Prioridade.ALTA);
        cenario(h6, u4, "Novelo cadastrado com sucesso", null,
                dado("que o estoque não tem o novelo \"Algodão Cru 100g\""),
                quando("a artesã cadastra o novelo \"Algodão Cru 100g\" com 5 unidades"),
                entao("o novelo aparece no estoque"),
                e("a quantidade em estoque é 5"));
        cenario(h6, u4, "Novelo repetido é rejeitado", null,
                dado("o novelo \"Algodão Cru 100g\" já cadastrado"),
                quando("a artesã cadastra outro novelo com o mesmo nome"),
                entao("o cadastro é rejeitado"),
                e("a mensagem informa novelo repetido"));
        sugestao(h6, u4, "Claude", "Sugira casos de borda para o cadastro de novelo",
                "Cenário: Quantidade negativa é rejeitada. Dado o novelo \"Algodão Cru 100g\", Quando a artesã cadastra a quantidade -2, Então o cadastro é rejeitado."); // PENDENTE

        Long h7 = historia(p, u4, "Baixa de material em uma peça", "artesã",
                "dar baixa nos novelos usados em uma peça", "manter o estoque atualizado", Prioridade.MEDIA);
        cenario(h7, u4, "Baixa com estoque suficiente", null,
                dado("o novelo \"Algodão Cru 100g\" com 5 unidades em estoque"),
                quando("a artesã usa 2 unidades na peça \"Amigurumi de coelho\""),
                entao("a quantidade em estoque passa a 3"));
        cenario(h7, u4, "Baixa maior que o estoque é rejeitada", null,
                dado("o novelo \"Algodão Cru 100g\" com 1 unidade em estoque"),
                quando("a artesã usa 2 unidades na peça \"Amigurumi de coelho\""),
                entao("a baixa é rejeitada"),
                e("a quantidade em estoque continua 1"));
        Long s10 = sugestao(h7, u3, "Gemini", "Sugira cenários para a baixa de material",
                "Dado que o estoque está correto");
        rejeitar(s10, u4, MotivoRejeicao.GENERICA, "Não traz um dado concreto para testar.");

        Long h8 = historia(p, u3, "Alerta de estoque baixo", "artesã",
                "ser avisada quando um novelo estiver acabando", "comprar antes de faltar material", Prioridade.BAIXA);
        cenario(h8, u3, "Alerta quando fica abaixo do mínimo", null,
                dado("o novelo \"Linha Azul Marinho 200g\" com mínimo 2 e 3 unidades em estoque"),
                quando("a artesã dá baixa em 2 unidades"),
                entao("o novelo \"Linha Azul Marinho 200g\" aparece na lista de estoque baixo"));
        cenario(h8, u3, "Sem alerta acima do mínimo", null,
                dado("o novelo \"Linha Azul Marinho 200g\" com mínimo 2 e 5 unidades em estoque"),
                quando("a artesã dá baixa em 1 unidade"),
                entao("o novelo \"Linha Azul Marinho 200g\" não aparece na lista de estoque baixo"));
    }

    // ------------------------------------------------------------------
    // Auxiliares (deixam os dados acima curtos e legíveis)
    // ------------------------------------------------------------------

    private Long historia(Long projeto, Long autor, String titulo, String papel,
                          String objetivo, String beneficio, Prioridade prioridade) {
        return historiaService.criar(projeto, autor, new HistoriaDto.Request(
                titulo, "Como " + papel, "Quero " + objetivo, "Para " + beneficio, prioridade)).id();
    }

    private void cenario(Long historia, Long autor, String titulo, OrigemCenario origem,
                         CenarioDto.PassoRequest... passos) {
        cenarioService.criar(historia, autor, new CenarioDto.Request(titulo, origem, List.of(passos)));
    }

    private Long sugestao(Long historia, Long usuario, String ferramenta, String prompt, String conteudo) {
        return sugestaoService.registrar(historia, usuario,
                new SugestaoDto.Request(ferramenta, prompt, conteudo)).id();
    }

    private void aceitar(Long sugestao, Long usuario, String justificativa) {
        sugestaoService.decidir(sugestao, usuario,
                new SugestaoDto.DecisaoRequest(DecisaoSugestao.ACEITA, null, justificativa));
    }

    private void rejeitar(Long sugestao, Long usuario, MotivoRejeicao motivo, String justificativa) {
        sugestaoService.decidir(sugestao, usuario,
                new SugestaoDto.DecisaoRequest(DecisaoSugestao.REJEITADA, motivo, justificativa));
    }

    private CenarioDto.PassoRequest dado(String t)   { return new CenarioDto.PassoRequest(TipoPasso.DADO, t); }
    private CenarioDto.PassoRequest quando(String t) { return new CenarioDto.PassoRequest(TipoPasso.QUANDO, t); }
    private CenarioDto.PassoRequest entao(String t)  { return new CenarioDto.PassoRequest(TipoPasso.ENTAO, t); }
    private CenarioDto.PassoRequest e(String t)      { return new CenarioDto.PassoRequest(TipoPasso.E, t); }
}