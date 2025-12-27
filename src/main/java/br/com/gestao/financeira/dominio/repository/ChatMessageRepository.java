package br.com.gestao.financeira.dominio.repository;

import br.com.gestao.financeira.dominio.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Find all messages for a user ordered by creation time.
     */
    List<ChatMessage> findByUsuarioIdOrderByCriadoEmAsc(Long usuarioId);

    /**
     * Find messages by session ID.
     */
    List<ChatMessage> findBySessionIdOrderByCriadoEmAsc(String sessionId);

    /**
     * Find recent messages for a user (for context).
     */
    @Query("SELECT c FROM ChatMessage c WHERE c.usuarioId = :usuarioId ORDER BY c.criadoEm DESC LIMIT 10")
    List<ChatMessage> findRecentByUsuarioId(Long usuarioId);

    /**
     * Delete all messages for a user.
     */
    void deleteByUsuarioId(Long usuarioId);

    /**
     * Delete messages by session ID.
     */
    void deleteBySessionId(String sessionId);
}
