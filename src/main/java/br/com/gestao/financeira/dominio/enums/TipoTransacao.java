package br.com.gestao.financeira.dominio.enums;

/**
 * Enum representando os tipos de transação financeira.
 */
public enum TipoTransacao {
    DEPOSITO("Depósito"),
    RETIRADA("Retirada"),
    TRANSFERENCIA("Transferência"),
    DESPESA("Despesa"),
    RECEITA("Receita");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
