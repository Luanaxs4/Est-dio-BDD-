## Documentação dos endpoints

Base: `http://localhost:8080`. Corpo e respostas em JSON (`Content-Type: application/json`).

- **Identificação do usuário:** as operações de escrita sobre histórias, cenários e sugestões exigem o cabeçalho `X-Usuario-Id` com o id do usuário


### Resumo

| Método | Rota | Cabeçalho | Descrição | Sucesso |
|---|---|---|---|---|
| POST | `/api/usuarios` | | Cria usuário | 201 |
| GET | `/api/usuarios` | | Lista usuários | 200 |
| GET | `/api/usuarios/{id}` | | Busca usuário | 200 |
| POST | `/api/projetos` | | Cria projeto | 201 |
| GET | `/api/projetos` | | Lista projetos | 200 |
| GET | `/api/projetos/{id}` | | Busca projeto | 200 |
| GET | `/api/projetos/{id}/resumo` | | Resumo do projeto (regra 7) | 200 |
| POST | `/api/projetos/{projetoId}/historias` | `X-Usuario-Id` | Cria história (quem envia vira o autor) | 201 |
| GET | `/api/projetos/{projetoId}/historias` | | Lista histórias com contagens; filtro `?prioridade=` (regra 6) | 200 |
| GET | `/api/historias/{id}` | | Busca história com cenários e passos | 200 |
| PUT | `/api/historias/{id}` | `X-Usuario-Id` | Atualiza história (só o autor) | 200 |
| DELETE | `/api/historias/{id}` | `X-Usuario-Id` | Exclui história, cenários e sugestões (só o autor) | 204 |
| GET | `/api/historias/{id}/feature` | | Exporta o arquivo `.feature` (regra 5) | 200 |
| POST | `/api/historias/{historiaId}/cenarios` | `X-Usuario-Id` | Cria cenário (só o autor) | 201 |
| GET | `/api/historias/{historiaId}/cenarios` | | Lista cenários da história | 200 |
| PUT | `/api/cenarios/{id}` | `X-Usuario-Id` | Atualiza cenário e substitui os passos (só o autor) | 200 |
| DELETE | `/api/cenarios/{id}` | `X-Usuario-Id` | Exclui cenário e renumera as posições (só o autor) | 204 |
| POST | `/api/historias/{historiaId}/sugestoes` | `X-Usuario-Id` | Registra sugestão (nasce `PENDENTE`) | 201 |
| GET | `/api/historias/{historiaId}/sugestoes` | | Lista sugestões; filtro `?decisao=` | 200 |
| PUT | `/api/sugestoes/{id}/decisao` | `X-Usuario-Id` | Aceita ou rejeita a sugestão | 200 |

---

### Usuários

**POST `/api/usuarios`**

```json
{
    "nome": "Ana Analista",
    "email": "ana@exemplo.com"
}
```

Resposta `201`:

```json
{
    "id": 1,
    "nome": "Ana Analista",
    "email": "ana@exemplo.com"
}
```

- `nome` e `email` obrigatórios; `email` válido e **único** (o sistema grava em minúsculas).
- Erros: `400` dados inválidos, `409` e-mail já cadastrado.

### Projetos

**POST `/api/projetos`**

```json
{
    "nome": "Estúdio BDD",
    "descricao": "Documentação de requisitos com histórias e cenários BDD",
    "contextoDominio": "História: Como/Quero/Para. Cenário: Dado/Quando/Então."
}
```

Resposta `201`:

```json
{
    "id": 1,
    "nome": "Estúdio BDD",
    "descricao": "Documentação de requisitos com histórias e cenários BDD",
    "contextoDominio": "História: Como/Quero/Para. Cenário: Dado/Quando/Então.",
    "dataCriacao": "2026-11-14"
}
```

- `nome` obrigatório e **único**. `descricao` e `contextoDominio` (glossário e informações do negócio) são opcionais.
- Erros: `400`, `409` nome já existe. `GET /api/projetos/{id}` retorna `404` se não existir.

**GET `/api/projetos/{id}/resumo`** (regra 7)

```json
{
    "projetoId": 1,
    "projetoNome": "Estúdio BDD",
    "totalHistorias": 5,
    "cenariosPorOrigem": { "HUMANO": 15, "SUGESTAO_IA": 1 },
    "sugestoesPorDecisao": { "PENDENTE": 3, "ACEITA": 1, "REJEITADA": 4 },
    "motivosRejeicaoMaisFrequentes": [
        { "motivo": "GENERICA", "quantidade": 1 },
        { "motivo": "DUPLICADA", "quantidade": 1 }
    ]
}
```

Os motivos vêm ordenados do mais para o menos frequente. Erro: `404` projeto não encontrado.

### Histórias de usuário

**POST `/api/projetos/{projetoId}/historias`** · Cabeçalho: `X-Usuario-Id: 1`

```json
{
    "titulo": "Fazer login",
    "papel": "Como analista",
    "objetivo": "Quero realizar o login",
    "beneficio": "Para acessar o sistema",
    "prioridade": "ALTA"
}
```

Resposta `201`:

```json
{
    "id": 1,
    "titulo": "Fazer login",
    "papel": "Como analista",
    "objetivo": "Quero realizar o login",
    "beneficio": "Para acessar o sistema",
    "prioridade": "ALTA",
    "dataCriacao": "2026-11-14",
    "dataAtualizacao": "2026-11-14",
    "projetoId": 1,
    "autorId": 1,
    "autorNome": "Ana Analista",
    "cenarios": []
}
```

- Todos os campos são obrigatórios. `titulo` é **único dentro do projeto**.
- O texto pode ou não começar com "Como", "Quero" e "Para"; a exportação não duplica esses prefixos.
- Erros: `400` campo ausente ou prioridade inválida, `404` projeto ou usuário inexistente, `409` título repetido no projeto.

**PUT `/api/historias/{id}`** · mesmo corpo do POST · `X-Usuario-Id` deve ser o **autor**.
Erros: `403` não é o autor, `404`, `409` título repetido.

**DELETE `/api/historias/{id}`** · `X-Usuario-Id` deve ser o **autor**. Remove também cenários, passos e sugestões. Resposta `204`. Erros: `403`, `404`.

**GET `/api/projetos/{projetoId}/historias`** (regra 6)

Parâmetro opcional: `?prioridade=ALTA` (ou `MEDIA`, `BAIXA`). Resposta `200`:

```json
[
    {
        "id": 1,
        "titulo": "Cadastro de cenários",
        "prioridade": "ALTA",
        "qtdCenarios": 4,
        "qtdSugestoesPendentes": 0
    }
]
```

Projeto sem histórias (ou filtro sem resultado) retorna `[]`; a mensagem "não há histórias" fica a cargo da interface. Erros: `400` prioridade inválida, `404` projeto inexistente.

**GET `/api/historias/{id}`** retorna a história completa (veja o exemplo acima), com a lista `cenarios` ordenada por posição e cada cenário com seus `passos`.

**GET `/api/historias/{id}/feature`** (regra 5)

Retorna `text/plain` como anexo (`Content-Disposition: attachment; filename="login-de-cliente.feature"`):

```gherkin
# language: pt
Funcionalidade: Login de cliente
  Como cliente cadastrado
  Quero entrar no sistema com e-mail e senha
  Para acessar meus pedidos

  Cenário: Login com sucesso
    Dado que estou na tela de login
    Quando informo e-mail e senha válidos
    Então vejo a página inicial
    E meu nome aparece no topo da tela
```

Erros: `404` história inexistente; `422` se a história **não tem cenário** ou tem **sugestões pendentes**.

### Cenários

**POST `/api/historias/{historiaId}/cenarios`** · `X-Usuario-Id` deve ser o **autor**

```json
{
    "titulo": "Login com sucesso",
    "origem": "HUMANO",
    "passos": [
        { "tipo": "DADO",   "texto": "que estou na tela de login" },
        { "tipo": "QUANDO", "texto": "informo e-mail e senha válidos" },
        { "tipo": "ENTAO",  "texto": "vejo a página inicial" },
        { "tipo": "E",      "texto": "meu nome aparece no topo da tela" }
    ]
}
```

Resposta `201`:

```json
{
    "id": 1,
    "titulo": "Login com sucesso",
    "posicao": 1,
    "origem": "HUMANO",
    "passos": [
        { "id": 1, "tipo": "DADO", "texto": "que estou na tela de login", "posicao": 1 },
        { "id": 2, "tipo": "QUANDO", "texto": "informo e-mail e senha válidos", "posicao": 2 }
    ]
}
```

- `titulo` obrigatório e **único na história**. `origem` é opcional (`HUMANO` por padrão; use `SUGESTAO_IA` ao incorporar uma sugestão aceita).
- A posição do cenário e a dos passos são automáticas (a ordem do array define a dos passos).
- **Regra 2:** é preciso ao menos um passo `QUANDO` e um `ENTAO`, na ordem Dado → Quando → Então; `E` e `MAS` continuam o tipo anterior e **não podem iniciar** o cenário.
- Erros: `400` corpo inválido, `403` não é o autor, `404`, `409` título repetido, `422` passos inválidos.

**PUT `/api/cenarios/{id}`** · mesmo corpo · substitui título, origem (se enviada) e **todos os passos** (os ids dos passos mudam). A posição do cenário não é alterada.

**DELETE `/api/cenarios/{id}`** · as posições dos cenários restantes são renumeradas. Resposta `204`.

**GET `/api/historias/{historiaId}/cenarios`** lista os cenários da história ordenados por posição.

### Sugestões de IA

**POST `/api/historias/{historiaId}/sugestoes`** · `X-Usuario-Id` (quem registra)

```json
{
    "ferramenta": "ChatGPT",
    "promptUtilizado": "Sugira cenários de erro para login de cliente",
    "conteudo": "Cenário: Login com senha incorreta. Dado que estou na tela de login, Quando informo senha incorreta, Então vejo mensagem de erro."
}
```

Resposta `201`:

```json
{
    "id": 1,
    "ferramenta": "ChatGPT",
    "promptUtilizado": "Sugira cenários de erro para login de cliente",
    "conteudo": "Cenário: Login com senha incorreta. ...",
    "decisao": "PENDENTE",
    "motivoRejeicao": null,
    "justificativa": null,
    "dataRegistro": "2026-11-14",
    "dataDecisao": null,
    "historiaId": 1,
    "registradaPorId": 1,
    "decididaPorId": null
}
```

- `ferramenta` e `conteudo` obrigatórios; `promptUtilizado` opcional. A sugestão **nasce `PENDENTE`**.
- Erros: `400`, `404` história ou usuário inexistente.

**PUT `/api/sugestoes/{id}/decisao`** · `X-Usuario-Id` (quem decide)

Aceitar:

```json
{
    "decisao": "ACEITA",
    "justificativa": "Cobre o fluxo de erro de senha."
}
```

Rejeitar (o motivo é obrigatório):

```json
{
    "decisao": "REJEITADA",
    "motivoRejeicao": "GENERICA",
    "justificativa": "Muito vaga, não é testável."
}
```

- `decisao` deve ser `ACEITA` ou `REJEITADA`. Motivos: `INCORRETA`, `GENERICA`, `DUPLICADA`, `FORA_DO_ESCOPO`, `OUTRO`.
- **Aceitar não altera os cenários.** O analista faz o ajuste pelos endpoints de cenário e pode marcar `"origem": "SUGESTAO_IA"`.
- Erros: `404`, `409` sugestão **já decidida** (não pode ser alterada), `422` decisão `PENDENTE` ou rejeição sem motivo.

**GET `/api/historias/{historiaId}/sugestoes`** · filtro opcional `?decisao=PENDENTE` (ou `ACEITA`, `REJEITADA`).

---

### Erros

Os erros seguem o formato `application/problem+json`:

```json
{
    "type": "about:blank",
    "title": "Unprocessable Entity",
    "status": 422,
    "detail": "O cenário precisa de ao menos um passo Então."
}
```

Em erros de validação (`400`), há também o campo `campos` com a mensagem de cada campo inválido:

```json
{
    "status": 400,
    "detail": "Dados inválidos.",
    "campos": { "titulo": "O título é obrigatório" }
}
```

| Código | Quando acontece |
|---|---|
| 400 | Campo obrigatório ausente, JSON ou enum inválido, cabeçalho `X-Usuario-Id` ausente |
| 403 | Usuário não é o autor da história |
| 404 | Recurso não encontrado |
| 409 | Duplicidade (e-mail, nome do projeto, título da história, título do cenário) ou sugestão já decidida |
| 422 | Regra de negócio violada (ordem dos passos, rejeição sem motivo, exportação bloqueada) |
