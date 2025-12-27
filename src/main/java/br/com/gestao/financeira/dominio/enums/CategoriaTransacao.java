package br.com.gestao.financeira.dominio.enums;

/**
 * Enum representando as categorias de transação para análise de despesas.
 */
public enum CategoriaTransacao {
    ALIMENTACAO("Alimentação"),
    MORADIA("Moradia"),
    TRANSPORTE("Transporte"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    EDUCACAO("Educação"),
    VESTUARIO("Vestuário"),
    SERVICOS("Serviços"),
    INVESTIMENTOS("Investimentos"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}





