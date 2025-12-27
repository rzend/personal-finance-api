package br.com.gestao.financeira.dominio.repository;

import java.math.BigDecimal;

/**
 * Port de saída para obtenção de saldo de conta bancária.
 * Integração com MockAPI externa.
 */
public interface SaldoRepository {

    /**
     * Obtém o saldo atual da conta de um usuário.
     * 
     * @param usuarioId o identificador do usuário
     * @return o saldo atual da conta
     */
    BigDecimal obterSaldoAtual(Long usuarioId);
}




