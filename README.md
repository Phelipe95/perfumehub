# PerfumeHub

API REST para gerenciamento de produtos, categorias e estoque de uma perfumaria.

Projeto de portfólio backend desenvolvido com Java e Spring Boot para aplicar conceitos de modelagem de domínio, persistência de dados, validação, arquitetura em camadas e testes automatizados.

## Tecnologias utilizadas

- Java 21 e Spring Boot 4.
- Maven, com Maven Wrapper incluído.
- PostgreSQL 17 e Docker Compose.
- Spring Data JPA e Hibernate.
- Flyway para versionamento do banco de dados.
- Jakarta Bean Validation.
- JUnit, Mockito e MockMvc.

## Funcionalidades implementadas

- Cadastro, listagem, consulta, atualização completa e exclusão de produtos.
- Cadastro, listagem e consulta de categorias.
- Relacionamento entre produtos e categorias.
- DTOs para entrada e saída de dados.
- Validação de nome obrigatório, preço e estoque não negativos e categoria obrigatória.
- Verificação da existência da categoria no cadastro e na atualização de produtos.
- Respostas padronizadas para dados inválidos, JSON malformado e recursos não encontrados.
- Persistência com Spring Data JPA e migrações com Flyway.
- Testes de entidade, serviços, controllers e persistência.

## Endpoints

URL base local: `http://localhost:8080`.

### Produtos

| Método | Endpoint | Operação | Sucesso |
| --- | --- | --- | --- |
| GET | `/produtos` | Listar produtos | 200 OK |
| GET | `/produtos/{id}` | Buscar produto por ID | 200 OK |
| POST | `/produtos` | Cadastrar produto | 201 Created |
| PUT | `/produtos/{id}` | Atualizar todos os campos editáveis | 200 OK |
| DELETE | `/produtos/{id}` | Excluir produto | 204 No Content |

### Categorias

| Método | Endpoint | Operação | Sucesso |
| --- | --- | --- | --- |
| GET | `/categorias` | Listar categorias | 200 OK |
| GET | `/categorias/{id}` | Buscar categoria por ID | 200 OK |
| POST | `/categorias` | Cadastrar categoria | 201 Created |

As requisições POST e PUT devem usar `Content-Type: application/json`.

## Exemplos de uso

### Cadastrar uma categoria

`POST /categorias`

```json
{
  "nome": "Masculino",
  "descricao": "Perfumes masculinos"
}
```

Use o ID retornado no cadastro para associar produtos a essa categoria.

### Cadastrar um produto

`POST /produtos`

```json
{
  "nome": "Malbec",
  "preco": 150.00,
  "quantidadeEstoque": 10,
  "categoriaId": 1
}
```

Exemplo de resposta com status `201 Created`, considerando os IDs ilustrativos:

```json
{
  "id": 1,
  "nome": "Malbec",
  "preco": 150.00,
  "quantidadeEstoque": 10,
  "categoriaId": 1
}
```

### Atualizar um produto

`PUT /produtos/1`

```json
{
  "nome": "Malbec Tradicional",
  "preco": 180.00,
  "quantidadeEstoque": 5,
  "categoriaId": 1
}
```

O PUT exige todos os quatro campos editáveis, preserva o ID e retorna o produto atualizado com status `200 OK`. A categoria informada deve existir. Preço e estoque podem ser zero.

### Excluir um produto

`DELETE /produtos/1`

A requisição não precisa de corpo. A exclusão bem-sucedida retorna `204 No Content`, sem corpo de resposta. Se o produto não existir, retorna `404 Not Found`.

## Tratamento de erros

| Status | Situação |
| --- | --- |
| 400 Bad Request | Campos inválidos ou JSON malformado/incompatível com os tipos esperados |
| 404 Not Found | Produto ou categoria não encontrado |

As respostas de erro usam um objeto com a lista `erros`. Exemplo:

```json
{
  "erros": [
    "Produto não encontrado."
  ]
}
```

Uma requisição com vários campos inválidos pode retornar várias mensagens. A ordem dessas mensagens não é garantida.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/br/com/perfumehub/
│   │   ├── categoria/
│   │   ├── exception/
│   │   └── produto/
│   └── resources/
│       ├── application.properties
│       └── db/migration/
└── test/
    ├── java/br/com/perfumehub/
    │   ├── categoria/
    │   └── produto/
    └── resources/
        └── application-test.properties
```

O código está organizado por domínio. Os pacotes de produtos e categorias reúnem entidades, DTOs, repositories, services e controllers. O pacote `exception` concentra o tratamento padronizado de erros.

## Como executar

### Pré-requisitos

- JDK 21.
- Docker e Docker Compose disponíveis e em execução.
- Porta 5432 disponível para o PostgreSQL e porta 8080 para a aplicação.

Os comandos abaixo usam PowerShell no Windows. Não é necessário instalar Maven separadamente, pois o projeto inclui o Maven Wrapper.

### 1. Clonar o repositório

```powershell
git clone https://github.com/Phelipe95/perfumehub.git
cd perfumehub
```

### 2. Definir a senha do banco

```powershell
$env:DB_PASSWORD = "sua_senha_local"
```

A variável é usada tanto pelo Docker Compose quanto pela aplicação. Execute os próximos comandos na mesma sessão do terminal.

Se o volume do PostgreSQL já tiver sido inicializado, use a senha configurada originalmente. Alterar a variável não altera automaticamente a senha do usuário no banco existente.

### 3. Iniciar o PostgreSQL

```powershell
docker compose up -d
docker compose ps
```

Aguarde o serviço ficar saudável (`healthy`). O Compose configura o banco `perfumehub`, o usuário `perfumehub` e um volume para persistir os dados.

### 4. Executar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`. O Flyway aplica as migrações, e o Hibernate valida o esquema do banco.

## Testes

A suíte chegou a **48 testes aprovados** após a implementação do PUT e do DELETE de produtos, conforme execução local.

- Entidades: validações e alterações de estado.
- Services: regras e interações com os repositories usando Mockito.
- Controllers: requisições HTTP, respostas e validações usando MockMvc.
- Persistência: gravação, consulta, atualização e exclusão com PostgreSQL.
- Aplicação: carregamento do contexto Spring.

### Preparar o banco de testes

Os testes de integração usam o perfil `test` e o banco `perfumehub_test`. O Compose atual cria apenas o banco principal; por isso, crie o banco de testes uma vez, com o PostgreSQL em execução:

```powershell
docker compose exec postgres createdb -U perfumehub perfumehub_test
```

Se `perfumehub_test` já existir, não é necessário recriá-lo. As tabelas são preparadas pelas migrações do Flyway durante a inicialização dos testes.

### Executar a suíte completa

Com o PostgreSQL disponível e `DB_PASSWORD` definida na mesma sessão:

```powershell
.\mvnw.cmd test
```

### Executar pelo IntelliJ IDEA

Defina `DB_PASSWORD` nas variáveis de ambiente da configuração que executará a aplicação ou os testes:

`Run → Edit Configurations → Environment variables`

Use a mesma senha do PostgreSQL local. Uma variável definida no PowerShell não é automaticamente recebida por uma IDE que já estava aberta. Configurações diferentes de execução podem precisar da variável separadamente.

## Próximas etapas

- Documentar a API com OpenAPI/Swagger.
- Evoluir as operações de gerenciamento de estoque.
- Ampliar as funcionalidades e a cobertura de testes de categorias.

## Status

Projeto em desenvolvimento, com CRUD de produtos concluído e testes automatizados.

## Autor

**Phelipe Bessa**

[GitHub](https://github.com/Phelipe95)
