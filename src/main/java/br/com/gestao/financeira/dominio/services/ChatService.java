package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.dominio.entity.ChatMessage;
import br.com.gestao.financeira.dominio.entity.ChatMessage.ChatRole;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.repository.ChatMessageRepository;
import br.com.gestao.financeira.dominio.repository.TransacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final TransacaoRepository transacaoRepository;
    private final RestTemplate restTemplate;

    @Value("${gpt4all.api.url:http://localhost:5000}")
    private String gpt4allApiUrl;

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            TransacaoRepository transacaoRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.transacaoRepository = transacaoRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Process a chat message and return the AI response.
     */
    @Transactional
    public String processMessage(Long usuarioId, String message, String sessionId) {
        // Save user message
        ChatMessage userMessage = new ChatMessage(usuarioId, ChatRole.USER, message, sessionId);
        chatMessageRepository.save(userMessage);

        // Build context with user financial data
        String context = buildFinancialContext(usuarioId);

        // Get AI response
        String aiResponse = callGpt4All(message, context);

        // Save assistant response
        ChatMessage assistantMessage = new ChatMessage(usuarioId, ChatRole.ASSISTANT, aiResponse, sessionId);
        chatMessageRepository.save(assistantMessage);

        return aiResponse;
    }

    /**
     * Build financial context for the user.
     */
    private String buildFinancialContext(Long usuarioId) {
        StringBuilder context = new StringBuilder();
        context.append("Contexto financeiro do usuário:\n");

        try {
            // Get last 30 days of transactions
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(30);

            List<Transacao> transacoes = transacaoRepository.findByUsuarioIdAndDataBetween(
                    usuarioId, startDate, endDate);

            if (transacoes.isEmpty()) {
                context.append("- Nenhuma transação nos últimos 30 dias.\n");
            } else {
                // Calculate totals
                BigDecimal totalReceitas = transacoes.stream()
                        .filter(t -> t.getTipo().name().equals("RECEITA") || t.getTipo().name().equals("DEPOSITO"))
                        .map(Transacao::getValorOriginal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalDespesas = transacoes.stream()
                        .filter(t -> t.getTipo().name().equals("DESPESA") || t.getTipo().name().equals("RETIRADA"))
                        .map(Transacao::getValorOriginal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal saldo = totalReceitas.subtract(totalDespesas);

                context.append(String.format("- Total de receitas (30 dias): R$ %.2f\n", totalReceitas));
                context.append(String.format("- Total de despesas (30 dias): R$ %.2f\n", totalDespesas));
                context.append(String.format("- Saldo do período: R$ %.2f\n", saldo));
                context.append(String.format("- Número de transações: %d\n", transacoes.size()));

                // Top categories
                Map<String, BigDecimal> byCategory = transacoes.stream()
                        .filter(t -> t.getTipo().name().equals("DESPESA"))
                        .collect(Collectors.groupingBy(
                                t -> t.getCategoria().name(),
                                Collectors.reducing(BigDecimal.ZERO, Transacao::getValorOriginal, BigDecimal::add)));

                if (!byCategory.isEmpty()) {
                    context.append("- Maiores categorias de despesa:\n");
                    byCategory.entrySet().stream()
                            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                            .limit(3)
                            .forEach(e -> context.append(String.format("  * %s: R$ %.2f\n", e.getKey(), e.getValue())));
                }

                // Recent transactions
                context.append("- Últimas 5 transações:\n");
                transacoes.stream()
                        .sorted(Comparator.comparing(Transacao::getData).reversed())
                        .limit(5)
                        .forEach(t -> context.append(String.format(
                                "  * %s: %s - R$ %.2f (%s)\n",
                                t.getData().format(DateTimeFormatter.ofPattern("dd/MM")),
                                t.getDescricao() != null ? t.getDescricao() : t.getCategoria().name(),
                                t.getValorOriginal(),
                                t.getTipo().name())));
            }
        } catch (Exception e) {
            logger.error("Error building financial context", e);
            context.append("- Não foi possível carregar dados financeiros.\n");
        }

        return context.toString();
    }

    /**
     * Call GPT4All API.
     */
    private String callGpt4All(String message, String financialContext) {
        try {
            String systemPrompt = String.format(
                    "Você é um assistente financeiro pessoal chamado FinBot. " +
                            "Responda sempre em português do Brasil. " +
                            "Seja educado, prestativo e objetivo. " +
                            "Use os dados financeiros do usuário para personalizar suas respostas quando relevante.\n\n%s",
                    financialContext);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", message);
            requestBody.put("system_prompt", systemPrompt);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    gpt4allApiUrl + "/chat",
                    HttpMethod.POST,
                    request,
                    Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("response");
            }

            return "Desculpe, não consegui processar sua mensagem. Tente novamente.";

        } catch (Exception e) {
            logger.error("Error calling GPT4All API", e);
            return "Desculpe, o serviço de IA está temporariamente indisponível. " +
                    "Por favor, tente novamente mais tarde ou verifique se o serviço GPT4All está ativo.";
        }
    }

    /**
     * Get chat history for a user.
     */
    public List<ChatMessage> getHistory(Long usuarioId) {
        return chatMessageRepository.findByUsuarioIdOrderByCriadoEmAsc(usuarioId);
    }

    /**
     * Get chat history by session.
     */
    public List<ChatMessage> getHistoryBySession(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCriadoEmAsc(sessionId);
    }

    /**
     * Clear chat history for a user.
     */
    @Transactional
    public void clearHistory(Long usuarioId) {
        chatMessageRepository.deleteByUsuarioId(usuarioId);
    }
}
