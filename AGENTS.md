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

**Mudanças:** Adicionou a classe `CarregadorDadosFicticios` na pasta `config`, a propriedade `app.seed.ativo` no `application.yml` e atualizou o `pom.xml`.

**Verificação:** ao subir com `app.seed.ativo: true`, o log confirma a carga e `GET /api/projetos` lista os projetos criados.

---

## SPA
