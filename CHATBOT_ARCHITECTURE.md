# Arquitetura do Chatbot com IA

Este documento detalha a implementação da funcionalidade de Chatbot, que segue uma arquitetura de microserviços para integrar inteligência artificial (LLM) aos dados financeiros do usuário.

## Visão Geral do Fluxo

O sistema utiliza uma arquitetura em três camadas para processar as mensagens de forma segura e contextualizada:

1. **Frontend (Angular)**: Interface do usuário e envio de mensagens.
2. **Backend (Java/Spring)**: Orquestrador, gerenciador de contexto e persistência.
3. **Microserviço AI (Python/FastAPI)**: Motor de inferência de IA usando GPT4All.

## 1. 🐍 O Microserviço Python (`gpt4all-service`)

Este serviço é o componente focado exclusivamente na inteligência artificial.

- **Tecnologia**: `FastAPI` para API REST de alta performance.
- **Motor de IA**: Utiliza a biblioteca `gpt4all` para carregar modelos LLM (como Orca Mini ou Llama) localmente na memória (CPU).
- **Isolamento**: Executa em um processo separado (container Docker próprio), garantindo que o processamento pesado da IA não afete a performance da API principal (Java) e permitindo escalabilidade independente.
- **Endpoint Inteligente**: Expõe a rota `POST /chat` que recebe um prompt, um prompt de sistema (instruções) e parâmetros de configuração, retornando a resposta gerada.

## 2. ☕ O Backend Java Integrador (`ChatService`)

O Backend Spring Boot atua como intermediário inteligente ("Middleware Pattern") e provedor de contexto.

- **Enriquecimento de Contexto (RAG Simplificado)**: Antes de contatar a IA, o `ChatService` consulta o banco de dados (`TransacaoRepository`) para buscar o resumo financeiro do usuário (saldo atual, despesas dos últimos 30 dias, totais por categoria).
- **Engenharia de Prompt**: Injeta os dados financeiros recuperados no "System Prompt". Isso permite que a IA forneça respostas personalizadas e baseadas em dados reais (ex: "Seu saldo atual é R$ X" ou "Você gastou muito em Alimentação").
- **Comunicação Segura**: O Backend Java atua como um gateway seguro. O serviço Python não é exposto publicamente; apenas a API Java consegue se comunicar com ele (via rede interna do Docker ou localhost).
- **Persistência**: Armazena todo o histórico da conversa (perguntas do usuário e respostas da IA) na tabela `chat_messages` via `ChatMessageRepository`.

## 3. 🎨 O Frontend Angular (`ChatComponent`)

O cliente web consome a API Java, mantendo a abstração completa do serviço de IA. O Frontend não sabe que existe um serviço Python.

- **Componentes**: 
    - `ChatComponent`: Gerencia a UI, estado de *loading* ("digitando...") e rolagem automática.
    - `ChatApiService`: Serviço que centraliza as chamadas HTTP para o endpoint `/api/chat` do Java.
- **Fluxo de Usuário**: Oferece uma experiência fluida onde o usuário interage com o bot como se fosse um chat convencional.

## Fluxo de Dados Detalhado (Step-by-Step)

1. **Usuário** digita e envia uma mensagem no Frontend.
2. **Frontend** envia uma requisição `POST /api/chat` para o Backend Java.
3. **Backend Java**:
    - Identifica o usuário autenticado via Token JWT.
    - Busca as transações e calcula o balanço financeiro do usuário.
    - Constrói o prompt final: *Instrução de Comportamento + Contexto Financeiro + Pergunta do Usuário*.
    - Envia o prompt via HTTP (`RestTemplate`) para o **Microserviço Python** (porta 5000).
4. **Microserviço Python**:
    - Recebe o prompt.
    - Processa a inferência no modelo GPT4All local.
    - Gera a resposta textual e devolve para o Java.
5. **Backend Java**:
    - Recebe a resposta da IA.
    - Salva a mensagem do usuário e a resposta da IA no banco de dados para histórico.
    - Retorna a resposta final para o Frontend.
6. **Frontend** exibe a resposta para o usuário e atualiza a lista de mensagens.
