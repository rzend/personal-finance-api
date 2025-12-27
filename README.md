# Gestão Financeira API

API de Gestão Financeira Pessoal desenvolvida com **Arquitetura Hexagonal** e **Spring Boot**. Este projeto fornece endpoints para gerenciamento de transações, análise de despesas, conversão de moedas e geração de relatórios.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.1**
- **PostgreSQL**: Banco de dados relacional
- **Spring Security + JWT**: Autenticação e Autorização
- **Swagger / OpenAPI**: Documentação da API
- **Apache POI**: Geração de relatórios Excel
- **iTextPDF**: Geração de relatórios PDF
- **Caffeine**: Cache para performance
- **Spring Retry**: Resiliência em integrações externas

## 🏗️ Arquitetura

O projeto segue a **Arquitetura Hexagonal** (Ports and Adapters), estruturada em:

- `dominio`: Regras de negócio, entidades e portas (interfaces)
- `aplicacao`: Casos de uso e coordenação (Serviços, Controllers/DTOs)
- `infraestrutura`: Implementações das portas (Repositórios, Integrações externas, Configurações)

## 📋 Pré-requisitos

- Java 17+
- Maven
- Docker & Docker Compose (Opcional, para ambiente containeirizado)

## 🏃 Como Executar

### Usando Docker (Recomendado)

Para subir a aplicação e o banco de dados PostgreSQL automaticamente:

```bash
docker-compose up -d --build
```

A API estará disponível em: `http://localhost:8080`

### Executando Localmente

1. Suba o banco de dados (pode usar o docker-compose apenas para o DB se preferir):
   ```bash
   docker-compose up -d db
   ```
2. Instale as dependências e faça o build:
   ```bash
   ./mvnw clean install
   ```
3. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

## 🔌 Endpoints Principais

A documentação completa pode ser acessada via **Swagger UI** após iniciar a aplicação:
👉 `http://localhost:8080/swagger-ui.html` (ou `/api-docs`)

### 🔐 Autenticação (`/auth`)
- `POST /auth/login`: Realiza login e retorna Token JWT
- `POST /auth/register`: Criação de novo usuário

### 💰 Transações (`/transacoes`)
- `GET /transacoes`: Listar transações (com filtros de data)
- `POST /transacoes`: Criar nova receita ou despesa
- `PUT /transacoes/{id}`: Atualizar transação
- `DELETE /transacoes/{id}`: Remover transação

### 📊 Análise (`/analise`)
- `GET /analise/despesas`: Relatórios analíticos de despesas por categoria
- `GET /analise/graficos`: Dados para gráficos do dashboard

### 💱 Câmbio (`/cambio`)
- `GET /cambio/cotacao`: Consultar taxas de câmbio (integração externa)

### 📑 Relatórios (`/relatorios`)
- `GET /relatorios/pdf`: Exportar extrato em PDF
- `GET /relatorios/excel`: Exportar extrato em Excel

### 👤 Usuário (`/usuarios`)
- `GET /usuarios/perfil`: Dados do usuário logado
- `PUT /usuarios/perfil`: Atualizar perfil

## 🧪 Testes

Para executar os testes unitários e de integração:

```bash
./mvnw test
```
