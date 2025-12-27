package br.com.gestao.financeira.dominio.repository;

import br.com.gestao.financeira.dominio.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para transações.
 */
@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long>,
                org.springframework.data.jpa.repository.JpaSpecificationExecutor<Transacao> {

        List<Transacao> findByUsuarioId(Long usuarioId);

        List<Transacao> findByUsuarioIdAndDataBetween(Long usuarioId, LocalDateTime startDate, LocalDateTime endDate);
}
