package br.com.gestao.financeira.aplicacao.dto;

import br.com.gestao.financeira.dominio.enums.StatusUsuario;
import java.time.LocalDateTime;

/**
 * DTO para resposta de usuário.
 */
public class UsuarioDto {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String moedaPadrao;
    private StatusUsuario status;
    private LocalDateTime criadoEm;
    private Long familiaId;

    public UsuarioDto() {
    }

    public UsuarioDto(Long id, String nomeCompleto, String email, String cpf,
            String moedaPadrao, StatusUsuario status, LocalDateTime criadoEm, Long familiaId) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.cpf = cpf;
        this.moedaPadrao = moedaPadrao;
        this.status = status;
        this.criadoEm = criadoEm;
        this.familiaId = familiaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getMoedaPadrao() {
        return moedaPadrao;
    }

    public void setMoedaPadrao(String moedaPadrao) {
        this.moedaPadrao = moedaPadrao;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getFamiliaId() {
        return familiaId;
    }

    public void setFamiliaId(Long familiaId) {
        this.familiaId = familiaId;
    }
}




