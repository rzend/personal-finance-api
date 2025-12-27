package br.com.gestao.financeira.dominio.modelo;

import java.util.Objects;

public class Moeda {
    private String codigo;
    private String nome;
    private String simbolo;
    private String tipoMoeda;

    public Moeda() {
    }

    public Moeda(String codigo, String nome, String simbolo) {
        this.codigo = codigo;
        this.nome = nome;
        this.simbolo = simbolo;
    }

    public Moeda(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getTipoMoeda() {
        return tipoMoeda;
    }

    public void setTipoMoeda(String tipoMoeda) {
        this.tipoMoeda = tipoMoeda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Moeda moeda = (Moeda) o;
        return Objects.equals(codigo, moeda.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}

