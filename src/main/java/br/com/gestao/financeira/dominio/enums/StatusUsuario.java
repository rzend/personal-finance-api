package br.com.gestao.financeira.dominio.enums;

/**
 * Enum representando os status possíveis de um usuário.
 */
public enum StatusUsuario {
    ATIVO("Ativo"),
    INATIVO("Inativo"),
    BLOQUEADO("Bloqueado");

    private final String descricao;

    StatusUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}





