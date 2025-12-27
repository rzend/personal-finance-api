# Gestão Financeira API

API de Gestão Financeira Pessoal desenvolvida com **Arquitetura Hexagonal** e **Spring Boot**. Este projeto fornece endpoints para gerenciamento de transações, análise de despesas, conversão de moedas e geração de relatórios.

## 🚀 Tecnologias Utilizadas

### Backend Principal (Java)
- **Java 17** & **Spring Boot 3.2.1**
- **Arquitetura Hexagonal** (Clean Architecture)
- **Spring Security** + **JWT**: Segurança robusta
- **PostgreSQL**: Persistência de dados
- **OpenAPI (Swagger)**: Documentação viva
- **Apache POI** & **iTextPDF**: Relatórios gerenciais
- **Caffeine Cache** & **Spring Retry**: Performance e Resiliência
- **Lombok**: Produtividade

### Microserviço de IA (Python)
- **Python 3.11+**
- **FastAPI**: Framework web de alta performance
- **GPT4All**: Inferência de LLMs locais (Privacy-first)

## 🏗️ Arquitetura

O projeto segue a **Arquitetura Hexagonal** (Ports and Adapters), estruturada em:

- `dominio`: Regras de negócio, entidades e portas (interfaces)
- `aplicacao`: Casos de uso e coordenação (Serviços, Controllers/DTOs)
- `infraestrutura`: Implementações das portas (Repositórios, Integrações externas, Configurações)

## 📋 Pré-requisitos

- Java 17+
- Maven
- Python 3.11+ (para o serviço de Chatbot)
- Docker & Docker Compose (Opcional, para ambiente containeirizado)

## 🏃 Como Executar

### Usando Docker (Recomendado)

Para subir a aplicação completa (API Java + Banco + Chatbot Python) automaticamente:

```bash
docker-compose up -d --build
```

A API estará disponível em: `http://localhost:8080`
O serviço de Chatbot (interno) estará em: `http://localhost:5000`

### Executando Localmente

1. **Banco de Dados**: Suba o banco PostgreSQL:
   ```bash
   docker-compose up -d db
   ```

2. **Serviço de Chatbot (Python)**:
   Em um terminal separado, navegue até a pasta `gpt4all-service`:
   ```bash
   cd gpt4all-service
   pip install -r requirements.txt
   python main.py
   ```

3. **API Backend (Java)**:
   Em outro terminal, na raiz do projeto:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

## 🤖 Chatbot IA (`/chat`)

O projeto inclui um assistente virtual inteligente capaz de responder perguntas sobre finanças e analisando o contexto (embora a integração completa com os dados do usuário esteja em desenvolvimento).

**Arquitetura do Chatbot:**
- **Microserviço Python**: Desenvolvido com **FastAPI** e **GPT4All**.
- **Modelos Locais**: Utiliza modelos LLM (Large Language Models) que rodam localmente na CPU, sem enviar dados para APIs externas (Privacidade total).
- **Comunicação**: A API Java se comunica com o serviço Python via HTTP REST.

### Endpoints do Chatbot
- `POST /chat/enviar`: Envia uma mensagem para o assistente e recebe a resposta.

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

### 💳 Saldo (`/saldo-conta`)
- `GET /saldo-conta`: Consulta de saldo atualizado do usuário

### 👨‍👩‍👧‍👦 Famílias (`/familias`)
- `POST /familias`: Criar um novo grupo familiar
- `POST /familias/{id}/membros`: Adicionar membros à família
- `GET /familias/meus-membros`: Listar integrantes da família

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
