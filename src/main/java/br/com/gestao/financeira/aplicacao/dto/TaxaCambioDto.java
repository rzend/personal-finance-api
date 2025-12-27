package br.com.gestao.financeira.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para informações de taxa de câmbio.
 */
public class TaxaCambioDto {

    private String moedaOrigem;
    private String moedaDestino;
    private BigDecimal taxa;
    private LocalDateTime obtidaEm;

    public TaxaCambioDto() {
    }

    public TaxaCambioDto(String moedaOrigem, String moedaDestino, BigDecimal taxa, LocalDateTime obtidaEm) {
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




