package br.com.gestao.financeira.aplicacao.dto;

/**
 * DTO para informações de moeda.
 */
public class MoedaDto {

    private String simbolo;
    private String nome;
    private String tipoMoeda;

    public MoedaDto() {
    }

    public MoedaDto(String simbolo, String nome, String tipoMoeda) {
        this.simbolo = simbolo;
        this.nome = nome;
        this.tipoMoeda = tipoMoeda;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoMoeda() {
        return tipoMoeda;
    }

    public void setTipoMoeda(String tipoMoeda) {
        this.tipoMoeda = tipoMoeda;
    }
}




