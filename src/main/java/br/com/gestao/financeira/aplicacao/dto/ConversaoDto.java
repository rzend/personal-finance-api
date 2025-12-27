package br.com.gestao.financeira.aplicacao.dto;

import java.math.BigDecimal;

/**
 * DTO para resultado de conversão de câmbio com custo total.
 */
public class ConversaoDto {

    private BigDecimal valorOriginal;
    private String moedaOrigem;
    private BigDecimal valorConvertido;
    private String moedaDestino;
    private BigDecimal taxa;
    private BigDecimal margemAplicada;
    private BigDecimal custoTotal;

    public ConversaoDto() {
    }

    public ConversaoDto(BigDecimal valorOriginal, String moedaOrigem, BigDecimal valorConvertido,
            String moedaDestino, BigDecimal taxa, BigDecimal margemAplicada,
            BigDecimal custoTotal) {
        this.valorOriginal = valorOriginal;
        this.moedaOrigem = moedaOrigem;
        this.valorConvertido = valorConvertido;
        this.moedaDestino = moedaDestino;
        this.taxa = taxa;
        this.margemAplicada = margemAplicada;
        this.custoTotal = custoTotal;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public String getMoedaOrigem() {
        return moedaOrigem;
    }

    public void setMoedaOrigem(String moedaOrigem) {
        this.moedaOrigem = moedaOrigem;
    }

    public BigDecimal getValorConvertido() {
        return valorConvertido;
    }

    public void setValorConvertido(BigDecimal valorConvertido) {
        this.valorConvertido = valorConvertido;
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

    public BigDecimal getMargemAplicada() {
        return margemAplicada;
    }

    public void setMargemAplicada(BigDecimal margemAplicada) {
        this.margemAplicada = margemAplicada;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }
}




