# PerfumeHub

API backend para gerenciamento de produtos, categorias e estoque de uma perfumaria.

O PerfumeHub é um projeto desenvolvido com Java e Spring Boot, com foco na aplicação prática de conceitos de desenvolvimento backend, persistência de dados, regras de negócio e testes automatizados.

O objetivo é construir uma aplicação organizada, testável e preparada para evoluir conforme novas funcionalidades forem implementadas.

## Tecnologias utilizadas

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat&logo=hibernate&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=flyway&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=flat&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=flat)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)

## Funcionalidades implementadas

- Cadastro das entidades Produto e Categoria.
- Relacionamento entre produtos e categorias.
- Validações de nome, preço, estoque e categoria.
- Persistência de dados com Spring Data JPA.
- Versionamento do banco de dados com Flyway.
- Operações iniciais na camada de serviços.
- Testes unitários com JUnit 5 e Mockito.
- Testes de integração utilizando PostgreSQL.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/br/com/perfumehub/
│   │   ├── categoria/
│   │   └── produto/
│   └── resources/
│       ├── application.properties
│       └── db/migration/
└── test/
    └── java/br/com/perfumehub/
        ├── categoria/
        └── produto/
```

O projeto está organizado por domínio, mantendo entidades, repositórios e serviços em seus respectivos pacotes.

## Como executar

### Pré-requisitos

- Java 21
- Docker e Docker Compose

### 1. Clonar o repositório

```bash
git clone https://github.com/Phelipe95/perfumehub.git
cd perfumehub
```

### 2. Configurar o banco de dados

Defina a variável de ambiente `DB_PASSWORD` com a senha que será utilizada pelo PostgreSQL.

No PowerShell:

```powershell
$env:DB_PASSWORD = "sua_senha_local"
```

### 3. Iniciar o PostgreSQL

```powershell
docker compose up -d
```

### 4. Executar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

O Spring Boot iniciará a aplicação e o Flyway verificará as migrações do banco de dados.

### 5. Executar os testes

```powershell
.\mvnw.cmd test
```

Os testes de integração utilizam PostgreSQL. Portanto, o banco de dados precisa estar disponível durante sua execução.

## Próximas etapas

- Implementação dos endpoints REST.
- Utilização de DTOs para entrada e saída de dados.
- Tratamento de exceções.
- Documentação da API com OpenAPI/Swagger.
- Evolução das operações de gerenciamento de estoque.

## Status

🚧 Projeto em desenvolvimento.

## Autor

**Phelipe Bessa**

[GitHub](https://github.com/Phelipe95)