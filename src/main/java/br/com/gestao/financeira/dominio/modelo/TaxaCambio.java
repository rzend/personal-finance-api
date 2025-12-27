package br.com.gestao.financeira.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TaxaCambio {
    private String moedaOrigem;
    private String moedaDestino;
    private BigDecimal taxa;
    private LocalDateTime obtidaEm;

    public TaxaCambio() {
    }

    public TaxaCambio(String moedaOrigem, String moedaDestino, BigDecimal taxa, LocalDateTime obtidaEm) {
        this.moedaOrigem = moedaOrigem;
        this.moedaDestino = moedaDestino;
        this.taxa = taxa;
        this.obtidaEm = obtidaEm;
    }

    public String getMoedaOrigem() {
        return moedaOrigem;
    }

    public void setMoedaOrigem(String moedaOrigem) {
        this.moedaOrigem = moedaOrigem;
    }

    public String getMoedaDestino() {
        return moedaDestino;
    }

    public void setMoedaDestino(String moedaDestino) {
        this.moedaDestino = moedaDestino;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public LocalDateTime getObtidaEm() {
        return obtidaEm;
    }

    public void setObtidaEm(LocalDateTime obtidaEm) {
        this.obtidaEm = obtidaEm;
    }
}


