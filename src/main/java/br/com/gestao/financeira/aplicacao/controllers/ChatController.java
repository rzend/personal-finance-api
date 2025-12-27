package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.dominio.entity.ChatMessage;
import br.com.gestao.financeira.dominio.services.ChatService;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

        private final ChatService chatService;
        private final UsuarioService usuarioService;

        public ChatController(ChatService chatService, UsuarioService usuarioService) {
                this.chatService = chatService;
                this.usuarioService = usuarioService;
        }

        /**
         * Send a message to the chatbot and receive a response.
         */
        @PostMapping
        public ResponseEntity<ChatResponseDto> chat(
                        Principal principal,
                        @Valid @RequestBody ChatRequestDto request) {
                Long usuarioId = getUsuarioId(principal);
                String sessionId = request.sessionId != null ? request.sessionId : UUID.randomUUID().toString();

                String response = chatService.processMessage(usuarioId, request.mensagem, sessionId);

                return ResponseEntity.ok(new ChatResponseDto(response, sessionId));
        }

        /**
         * Get chat history for the current user.
         */
        @GetMapping("/historico")
        public ResponseEntity<List<ChatMessageDto>> getHistory(Principal principal) {
                Long usuarioId = getUsuarioId(principal);
                List<ChatMessage> messages = chatService.getHistory(usuarioId);

                List<ChatMessageDto> dtos = messages.stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(dtos);
        }

        /**
         * Get chat history by session ID.
         */
        @GetMapping("/historico/{sessionId}")
        public ResponseEntity<List<ChatMessageDto>> getHistoryBySession(
                        @PathVariable String sessionId) {
                List<ChatMessage> messages = chatService.getHistoryBySession(sessionId);

                List<ChatMessageDto> dtos = messages.stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(dtos);
        }

        /**
         * Clear chat history for the current user.
         */
        @DeleteMapping("/historico")
        public ResponseEntity<Void> clearHistory(Principal principal) {
                Long usuarioId = getUsuarioId(principal);
                chatService.clearHistory(usuarioId);
                return ResponseEntity.noContent().build();
        }

        private Long getUsuarioId(Principal principal) {
                return usuarioService.buscarPorEmail(principal.getName()).getId();
        }

        private ChatMessageDto toDto(ChatMessage message) {
                return new ChatMessageDto(
                                message.getId(),
                                message.getRole().name().toLowerCase(),
                                message.getContent(),
                                message.getCriadoEm(),
                                message.getSessionId());
        }

        // DTOs
        public record ChatRequestDto(
                        @NotBlank(message = "Mensagem não pode estar vazia") String mensagem,
                        String sessionId) {
        }

        public record ChatResponseDto(
                        String resposta,
                        String sessionId) {
        }

        public record ChatMessageDto(
                        Long id,
                        String role,
                        String content,
                        LocalDateTime criadoEm,
                        String sessionId) {
        }
}
