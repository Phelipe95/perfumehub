# PerfumeHub

Aplicação para controle de produtos, categorias e compras de uma perfumaria, com API REST em Java e interface em React.

O projeto começou como portfólio backend e está evoluindo para ajudar no controle de uma pequena perfumaria: consultar o estoque, registrar compras com custos diferentes e, futuramente, registrar vendas e acompanhar resultados.

## Tecnologias utilizadas

### Backend

- Java 21 e Spring Boot 4.
- Maven, com Maven Wrapper incluído.
- PostgreSQL 17 e Docker Compose.
- Spring Data JPA e Hibernate.
- Flyway para versionamento do banco de dados.
- Jakarta Bean Validation.
- JUnit, Mockito e MockMvc.

### Frontend

- React e JavaScript.
- Vite.
- CSS Modules e variáveis CSS para o tema.
- ESLint.
- Fetch API para comunicação com o backend.

## Funcionalidades implementadas

### API

- Cadastro, listagem, consulta, atualização e exclusão de produtos e categorias.
- Associação de produtos a categorias existentes.
- Verificação de nome duplicado na atualização de categorias.
- Bloqueio da exclusão de categorias com produtos vinculados.
- Registro de entradas de estoque com produto, data da compra, quantidade e custo unitário.
- Atualização do saldo e cálculo do custo médio ponderado nas entradas de estoque.
- Registro da entrada e atualização do produto na mesma transação.
- DTOs para entrada e saída de dados.
- Validação de campos e respostas padronizadas para os erros tratados.
- Persistência com Spring Data JPA e migrações com Flyway.

### Interface

- Catálogo de produtos carregado da API.
- Busca por nome e filtro por categoria.
- Mensagem quando nenhum produto corresponde aos filtros.
- Exibição de preço de venda, categoria e quantidade em estoque.
- Estados de carregamento e erro na consulta do catálogo.
- Modal de novo produto com campos para compra inicial.
- Cadastro de categoria dentro do formulário de produto, com atualização da lista e seleção da categoria criada.
- Organização em componentes, página, template e hook de catálogo.

O envio do formulário de produto e as ações de editar, excluir, registrar compra e registrar venda ainda estão em desenvolvimento na interface. As telas de vendas e relatórios também estão planejadas.

## Endpoints

URL base local: `http://localhost:8080`.

### Produtos

| Método | Endpoint | Operação | Sucesso |
| --- | --- | --- | --- |
| GET | `/produtos` | Listar produtos | 200 OK |
| GET | `/produtos/{id}` | Buscar produto por ID | 200 OK |
| POST | `/produtos` | Cadastrar produto | 201 Created |
| PUT | `/produtos/{id}` | Atualizar produto | 200 OK |
| DELETE | `/produtos/{id}` | Excluir produto | 204 No Content |

### Categorias

| Método | Endpoint | Operação | Sucesso |
| --- | --- | --- | --- |
| GET | `/categorias` | Listar categorias | 200 OK |
| GET | `/categorias/{id}` | Buscar categoria por ID | 200 OK |
| POST | `/categorias` | Cadastrar categoria | 201 Created |
| PUT | `/categorias/{id}` | Atualizar categoria | 200 OK |
| DELETE | `/categorias/{id}` | Excluir categoria sem produtos vinculados | 204 No Content |

### Entradas de estoque

| Método | Endpoint | Operação | Sucesso |
| --- | --- | --- | --- |
| POST | `/entradas-estoque` | Registrar compra e atualizar estoque e custo médio | 201 Created |

As requisições POST e PUT devem usar `Content-Type: application/json`. Os IDs dos exemplos abaixo são ilustrativos; utilize os retornados pela API.

## Exemplos de uso

### 1. Cadastrar uma categoria

`POST /categorias`

```json
{
  "nome": "Masculino",
  "descricao": "Perfumes masculinos"
}
```

### 2. Cadastrar um produto

`POST /produtos`

```json
{
  "nome": "Malbec",
  "preco": 150.00,
  "quantidadeEstoque": 0,
  "categoriaId": 1
}
```

Para o fluxo de compras, cadastre o produto com estoque zero e registre a entrada em seguida. Assim, a quantidade adquirida fica associada ao custo e à data da compra.

### 3. Registrar uma compra

`POST /entradas-estoque`

```json
{
  "produtoId": 1,
  "dataCompra": "2026-11-08",
  "quantidade": 10,
  "custoUnitario": 100.00
}
```

A quantidade deve ser maior que zero. O custo unitário pode ser zero e deve ter até oito dígitos inteiros e duas casas decimais.

Cada compra gera uma entrada para o mesmo produto, preservando seu custo unitário no histórico. Não é necessário duplicar o produto quando o fornecedor altera o preço.

O custo médio considera o saldo anterior e a nova compra:

```text
novo custo médio =
  (estoque atual × custo médio atual + quantidade comprada × custo unitário)
  ÷ (estoque atual + quantidade comprada)
```

Por exemplo, duas unidades a R$ 100,00 e mais dez a R$ 80,00 resultam em 12 unidades com custo médio de R$ 83,333333. O cálculo usa seis casas decimais. O preço de venda permanece separado do custo de compra.

### 4. Atualizar uma categoria

`PUT /categorias/1`

```json
{
  "nome": "Masculino",
  "descricao": "Perfumes e fragrâncias masculinas"
}
```

### 5. Atualizar um produto

`PUT /produtos/1`

```json
{
  "nome": "Malbec Tradicional",
  "preco": 180.00,
  "quantidadeEstoque": 10,
  "categoriaId": 1
}
```

O contrato atual exige os quatro campos, preserva o ID e retorna o produto atualizado. A quantidade ainda pode ser alterada diretamente pelo PUT; essa regra está em revisão para manter a consistência com o histórico de compras.

### 6. Excluir uma categoria

`DELETE /categorias/1`

A requisição não precisa de corpo. Uma categoria sem produtos vinculados pode ser excluída com retorno `204 No Content`. Se houver produtos vinculados, retorna `409 Conflict`.

## Tratamento de erros

| Status | Situação tratada |
| --- | --- |
| 400 Bad Request | Falha de validação dos DTOs ou JSON malformado/incompatível |
| 404 Not Found | Produto ou categoria não encontrado |
| 409 Conflict | Nome já utilizado por outra categoria na atualização ou tentativa de excluir categoria com produtos vinculados |

As respostas padronizadas usam uma lista `erros`:

```json
{
  "erros": [
    "Categoria não encontrada."
  ]
}
```

Uma requisição com vários campos inválidos pode retornar várias mensagens. A ordem dessas mensagens não é garantida.

O tratamento de conflitos ainda será ampliado. No estado atual, cadastrar uma categoria com nome duplicado pode retornar HTTP 500 por violação da restrição do banco.

## Estrutura do projeto

```text
perfumehub/
├── src/
│   ├── main/
│   │   ├── java/br/com/perfumehub/
│   │   │   ├── categoria/
│   │   │   ├── estoque/
│   │   │   ├── exception/
│   │   │   └── produto/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   └── test/
│       ├── java/br/com/perfumehub/
│       └── resources/application-test.properties
├── frontend/
│   ├── public/
│   └── src/
│       ├── components/
│       ├── hooks/
│       ├── pages/
│       ├── styles/
│       ├── templates/
│       ├── App.jsx
│       └── main.jsx
├── compose.yaml
└── pom.xml
```

O backend está organizado por domínio, com entidades, DTOs, repositories, services e controllers. O pacote `exception` concentra o tratamento de erros.

No frontend, os componentes usam arquivos `index.jsx` e `styles.module.css`. Os estilos globais e o tema ficam em `src/styles`, e o hook `useCatalog` concentra o carregamento do catálogo.

## Como executar

### Pré-requisitos

- JDK 21.
- Docker e Docker Compose disponíveis e em execução.
- Node.js compatível com a versão do Vite e npm. O ambiente de desenvolvimento utilizado tem Node.js 24 e npm 11.
- Portas 5432, 8080 e 5173 disponíveis.

Os comandos abaixo usam PowerShell no Windows. O Maven Wrapper está incluído no projeto.

### 1. Clonar o repositório

```powershell
git clone https://github.com/Phelipe95/perfumehub.git
cd perfumehub
```

### 2. Configurar a senha do banco

```powershell
$env:DB_PASSWORD = "sua_senha_local"
```

A variável é usada pelo Docker Compose e pela aplicação. Execute os comandos do backend na mesma sessão do terminal.

Se o volume do PostgreSQL já estiver inicializado, use a senha configurada originalmente. Alterar a variável não altera automaticamente a senha do usuário no banco existente.

### 3. Iniciar o PostgreSQL

```powershell
docker compose up -d
docker compose ps
```

Aguarde o serviço ficar saudável (`healthy`). O Compose configura o banco `perfumehub`, o usuário `perfumehub` e um volume para persistir os dados.

### 4. Executar o backend

Na raiz do repositório:

```powershell
.\mvnw.cmd spring-boot:run
```

A API fica disponível em `http://localhost:8080`. O Flyway aplica as migrações e o Hibernate valida o esquema.

Os arquivos SQL de migração fazem parte do código e devem ser versionados. Migrações já aplicadas devem ser preservadas; novas alterações do esquema devem usar novas versões.

### 5. Executar o frontend

Em outro terminal, na raiz do repositório:

```powershell
cd frontend
npm ci
npm run dev
```

Acesse o endereço informado pelo Vite, normalmente `http://localhost:5173`.

Durante o desenvolvimento, o frontend chama `/api/produtos`, `/api/categorias` e os demais caminhos da API. O proxy do Vite remove o prefixo `/api` e encaminha as requisições para `http://localhost:8080`.

Essa configuração de proxy é de desenvolvimento. O encaminhamento para a API em produção ainda precisa ser configurado na hospedagem.

## Testes e verificações

A última execução completa informada no desenvolvimento teve **88 testes de backend aprovados**, sem falhas ou erros.

A suíte inclui:

- Validações e alterações de estado das entidades.
- Regras de services e interações com repositories usando Mockito.
- Requisições, respostas e validações de controllers com MockMvc.
- Persistência com PostgreSQL.
- Registro de entradas, cálculo de custo médio e rollback quando a gravação falha.
- Carregamento do contexto Spring.

### Preparar o banco de testes

Os testes de integração usam o perfil `test` e o banco `perfumehub_test`. O Compose cria apenas o banco principal. Crie o banco de testes uma vez:

```powershell
docker compose exec postgres createdb -U perfumehub perfumehub_test
```

Se o banco já existir, não é necessário recriá-lo. O Flyway prepara suas tabelas durante a inicialização dos testes.

### Executar os testes do backend

Na raiz, com PostgreSQL disponível e `DB_PASSWORD` definida:

```powershell
.\mvnw.cmd test
```

### Executar pelo IntelliJ IDEA

Defina `DB_PASSWORD` nas variáveis de ambiente da configuração que executará a aplicação ou os testes:

`Run → Edit Configurations → Environment variables`

Use a mesma senha do PostgreSQL local. Uma variável definida no PowerShell não é automaticamente recebida por uma IDE que já estava aberta.

### Verificar o frontend

Dentro de `frontend`:

```powershell
npm run lint
npm run build
```

Esses comandos verificam o código com ESLint e geram o build. Ainda não há uma suíte de testes automatizados do frontend configurada.

## Próximas etapas

- Corrigir o conflito de nome duplicado no cadastro de categorias.
- Integrar o cadastro de produtos e a compra inicial pela interface.
- Integrar edição, exclusão e registro de compras na interface.
- Revisar alterações diretas de estoque e exclusão de produtos com histórico de compras.
- Proteger o saldo contra atualizações simultâneas.
- Implementar registro de vendas, com cliente identificado ou avulso.
- Implementar relatórios por período, com dashboard e exportação de planilha.
- Documentar a API com OpenAPI/Swagger.

## Status

Projeto em desenvolvimento: CRUDs de produtos e categorias e registro de entradas disponíveis na API; frontend integrado ao catálogo e ao cadastro de categorias, com os demais fluxos em construção.

## Autor

**Phelipe Bessa**

[GitHub](https://github.com/Phelipe95)

