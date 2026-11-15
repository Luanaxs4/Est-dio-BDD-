# Uso orientado de IA

Registro das interações relevantes com ferramentas de IA ao longo do projeto. Os dados usados nos prompts são fictícios e nenhuma credencial foi enviada.

---

## API

### 1. Revisão das entidades e do diagrama

**Problema:** Conferir se as entidades JPA estavam de acordo com o diagrama de classes e com a especificação.

**Ferramenta:** Claude (Anthropic)

**Prompt:** "Dadas as entidades, verifique se está correto e, se precisar, corrija." (Diagrama, especificação e 6 entidades anexados.)

**Proposta da IA:** Apontou `Sugestao` ligada a `Projeto` em vez de `HistoriaUsuario`, a falta de `decididaPor`, o autor mapeado de forma errada, os `String` que deveriam ser enums e as restrições de unicidade que faltavam.

**Decisão do grupo:** Aceitar a proposta com ajustes.

**Mudanças:** Classes corrigidas, 5 enums criados e diagrama ajustado.

**Verificação:** aplicação sobe sem erro e as tabelas são criadas conforme o diagrama.

---

### 2. Services e controllers

**Problema:** Implementar a camada de serviços e os controllers seguindo as regras de negócio.

**Ferramenta:** Claude (Anthropic)

**Prompt:** "Com as entidades corrigidas, gere os services e controllers seguindo as regras de negócio." (Repositórios e regras de negócio anexados.)

**Proposta da IA:** Services e controllers gerados, com novos métodos nos repositórios e DTOs de entrada e saída.

**Decisão do grupo:** Aceitar a proposta com ajustes.

**Mudanças:** Em `ResumoProjeto`, troca de `List<ResumoProjeto.MotivoContagem>` para `Map<MotivoRejeicao, Long>`, porque o método `resumo()` de `ProjetoService` já usava esse padrão de mapa (chave e valor) para a origem do cenário e a decisão da sugestão.

**Verificação:** coleção do Postman executada, com a quantidade de requisições que retornaram o status esperado; conferência do `GET /api/projetos/1/resumo`.

---

### 3. Bug do id nulo nos passos

**Problema:** Ao atualizar um cenário (`PUT /api/cenarios/{id}`), o `id` dos passos voltava `null` na resposta.

**Ferramenta:** Claude (Anthropic)

**Prompt:** "Ao atualizar, o id de passos fica null, por quê?" (com o print do Postman), seguido de "Posso usar `save()` em vez de `flush()`?".

**Proposta da IA:** O id só é gerado na gravação, que acontece depois da resposta. Sugeriu `repository.flush()` e explicou a alternativa com `save()`.

**Decisão do grupo:** Optou por usar `save()`.

**Mudanças:** No método `atualizar` da classe `CenarioService`, adicionou `repository.save(cenario)` antes do `return`.

**Verificação:**  `PUT /api/cenarios/{id}` no Postman, com os ids dos passos preenchidos na resposta.

---

### 4. Dados fictícios

**Problema:** Popular o banco com dados de exemplo ao iniciar a aplicação, para testar sem cadastrar tudo manualmente.

**Ferramenta:** Claude (Anthropic)

**Prompt:** "Assim que iniciar a aplicação, tem como subir dados automaticamente?", seguido de "Gere a classe de configuração com dados fictícios".

**Proposta da IA:** Explicou que a automação é possível e, em seguida, gerou a classe e explicou como adicioná-la ao projeto.

**Decisão do grupo:** Aceitou a proposta.

**Mudanças:** Adicionou a classe `CarregadorDadosFicticios` na pasta `config`, a propriedade `app.seed.ativo` no `application.yml`.

**Verificação:** ao subir com `app.seed.ativo: true`, o log confirma a carga e `GET /api/projetos` lista os projetos criados.

---

## SPA

### 1. Inclusão do campo de Contexto de Domínio no cadastro de projetos

**Problema:** Era necessário permitir que o usuário registrasse um glossário ou contexto de domínio ao criar um projeto para apoiar a escrita dos cenários BDD, o que não estava contemplado no cadastro inicial.

**Ferramenta:** Gemini (Google)

**Prompt:** "Adicione o campo `contextoDominio` no cadastro de projetos, ajustando desde o formulário do frontend até o controller, service e banco de dados."

**Proposta da IA:** Adição do campo `textarea` no modal de criação de projeto no React e atualização dos DTOs (`ProjetoRequest`/`ProjetoResponse`), camada de serviço (`ProjetoService`), entidade JPA e coluna na tabela `PROJETO`.

**Decisão do grupo:** Aceitar a proposta.

**Mudanças:** Atualização de `ProjetosPage.tsx`, `ProjetoDetalhesPage.tsx`, `ProjetoController.java`, `ProjetoService.java` e tabela `PROJETO`.

**Verificação:** cadastro de um novo projeto com texto no campo "Contexto do Domínio / Glossário" e confirmação da exibição destacada na tela de detalhes do projeto.

---

### 2. Padronização de modais e sobreposição na interface (`ModalAviso`)

**Problema:** A aplicação utilizava diálogos nativos do navegador (`alert` e `window.confirm`) para erros e confirmações, quebrando o padrão visual, além de o modal de aviso ficar escondido atrás do modal de criação de projeto por limitação de contexto de empilhamento (`z-index`).

**Ferramenta:** Gemini (Google)

**Prompt:** "Substitua os pop-ups nativos (`alert` e `window.confirm`) pelo componente `ModalAviso` em todas as páginas, adaptando-o para suportar tanto avisos simples quanto confirmações, e garanta que ele apareça por cima de outros modais abertos na tela."

**Proposta da IA:** Refatoração do `ModalAviso` para operar em dois modos (aviso com botão "OK" e confirmação via `onConfirmar`) e renderização fora da hierarquia do componente pai usando `createPortal(..., document.body)` com `zIndex: 9999`.

**Decisão do grupo:** Aceitar a proposta.

**Mudanças:** Atualização de `ModalAviso.tsx` com React Portal e substituição de todas as chamadas nativas em `ProjetosPage.tsx` e `EstudioHistoriaPage.tsx`.

**Verificação:** teste manual tentando cadastrar um projeto com nome duplicado dentro do modal "Novo Projeto", confirmando que o alerta de erro aparece sobreposto em primeiro plano, e exclusão de histórias/cenários usando o modal padronizado.

---

### 3. Controle de visibilidade de ações conforme autoria da história

**Problema:** Os botões de edição e exclusão de histórias e cenários, além do botão de criação de novos cenários, estavam visíveis para qualquer usuário autenticado, mesmo quando ele não era o autor da história de usuário.

**Ferramenta:** Gemini (Google)

**Prompt:** "Oculte os botões de editar e excluir história e cenários, bem como o botão '+ Novo Cenário', quando o usuário logado não for o autor da história." (Código de `EstudioHistoriaPage.tsx` anexado.)

**Proposta da IA:** Criação da verificação `ehAutor` comparando `historia.autorId` com `usuarioAtual.id` e aplicação de renderização condicional nos botões de ação e nos formulários de edição da história e de cenários.

**Decisão do grupo:** Aceitar a proposta.

**Mudanças:** Condicional `{ehAutor && ...}` aplicada nos controles de edição/exclusão da história, nos botões de cada cenário e no botão/formulário de `+ Novo Cenário` em `EstudioHistoriaPage.tsx`.

**Verificação:** alternância entre usuários diferentes no seletor do cabeçalho, validando que o autor visualiza todos os controles e outros usuários acessam a história apenas em modo leitura (mantendo o registro e avaliação de sugestões de IA).
