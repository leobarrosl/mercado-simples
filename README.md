# Mercado Simples

Aplicação backend para um sistema simples de gerenciamento de mercado desenvolvido com Spring Boot.

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação principal
- **Spring Boot**: Framework para desenvolvimento de aplicações Java
- **Spring Data JPA**: Abstração para acesso a dados
- **MariaDB**: Banco de dados relacional usado em produção
- **Flyway**: Gerenciamento de migrações de banco de dados
- **Maven**: Gerenciamento de dependências e build
- **Docker & Docker Compose**: Containerização da aplicação
- **OpenAPI/Swagger**: Documentação de API automática
- **H2**: Banco de dados em memória usado para testes

## Estrutura do Projeto

A aplicação segue uma arquitetura em camadas:
- **Controllers**: Endpoints da API REST
- **Services**: Lógica de negócios
- **Repositories**: Acesso a dados 
- **Models**: Entidades JPA
- **DTOs**: Objetos de transferência de dados

## Testes

O projeto inclui:
- **Testes unitários**: Para validação de componentes individuais
- **Testes de integração**: Com banco de dados H2 em memória
- **Configuração específica para testes**: Usando o perfil "test" do Spring

Os testes utilizam JUnit e são executados automaticamente durante o build Maven.

## Docker

A aplicação é containerizada com Docker:
- Imagem base: eclipse-temurin:21-jre
- Inclui script `wait-for-it.sh` para garantir que o banco de dados esteja disponível antes de iniciar a aplicação
- Docker Compose para orquestrar a aplicação e o banco de dados

## Como Executar

### Com Docker Compose
```bash
docker-compose up -d
```

### Localmente
```bash
mvn spring-boot:run
```

### Acessar a API
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Configuração

As principais configurações estão em `application.properties` e podem ser sobrescritas por variáveis de ambiente:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
