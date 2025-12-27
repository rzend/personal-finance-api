package br.com.gestao.financeira.infraestrutura.integrations;

import br.com.gestao.financeira.dominio.repository.SaldoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador mock para simulação de API de saldo bancário.
 * Em ambiente de produção, este seria substituído por uma integração real.
 */
@Component
public class MockApiSaldoAdapter implements SaldoRepository {

    private static final Logger log = LoggerFactory.getLogger(MockApiSaldoAdapter.class);

    // Cache simples para manter consistência entre chamadas
    private final Map<Long, BigDecimal> saldosSimulados = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Override
    public BigDecimal obterSaldoAtual(Long usuarioId) {
        log.info("Obtendo saldo mock para usuário: {}", usuarioId);

        // Retorna saldo consistente para o mesmo usuário
        return saldosSimulados.computeIfAbsent(usuarioId, this::gerarSaldoAleatorio);
    }

    /**
     * Gera um saldo aleatório entre R$ 100 e R$ 50.000.
     */
    private BigDecimal gerarSaldoAleatorio(Long usuarioId) {
        double saldo = 100 + (random.nextDouble() * 49900);
        BigDecimal saldoFormatado = BigDecimal.valueOf(saldo).setScale(2, RoundingMode.HALF_UP);
        log.debug("Saldo gerado para usuário {}: R$ {}", usuarioId, saldoFormatado);
        return saldoFormatado;
    }

    /**
     * Permite atualizar o saldo simulado (útil para testes).
     */
    public void definirSaldo(Long usuarioId, BigDecimal saldo) {
        saldosSimulados.put(usuarioId, saldo);
    }

    /**
     * Limpa todos os saldos simulados (útil para testes).
     */
    public void limparSaldos() {
        saldosSimulados.clear();
    }
}





