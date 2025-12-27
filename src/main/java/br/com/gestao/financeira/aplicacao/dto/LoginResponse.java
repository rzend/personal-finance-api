package br.com.gestao.financeira.aplicacao.dto;

/**
 * DTO para resposta de login com token JWT.
 */
public class LoginResponse {

    private String token;
    private String tipo;
    private Long usuarioId;
    private String email;
    private String nomeCompleto;

    public LoginResponse() {
        this.tipo = "Bearer";
    }

    public LoginResponse(String token, Long usuarioId, String email, String nomeCompleto) {
        this.token = token;
        this.tipo = "Bearer";
        this.usuarioId = usuarioId;
        this.email = email;
        this.nomeCompleto = nomeCompleto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }
}




