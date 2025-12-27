package br.com.gestao.financeira.dominio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatRole role;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "session_id")
    private String sessionId;

    public enum ChatRole {
        USER, ASSISTANT, SYSTEM
    }

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }

    // Constructors
    public ChatMessage() {
    }

    public ChatMessage(Long usuarioId, ChatRole role, String content, String sessionId) {
        this.usuarioId = usuarioId;
        this.role = role;
        this.content = content;
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public ChatRole getRole() {
        return role;
    }

    public void setRole(ChatRole role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
