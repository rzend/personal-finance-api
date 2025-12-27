package br.com.gestao.financeira.dominio.enums;

/**
 * Enum representando os perfis de acesso de um usuário (Roles).
 */
public enum PerfilUsuario {
    USUARIO("ROLE_USUARIO"),
    GESTOR("ROLE_GESTOR"),
    MASTER("ROLE_MASTER");

    private final String role;

    PerfilUsuario(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
