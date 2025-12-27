package br.com.gestao.financeira.aplicacao.dto;

import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.enums.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de transação.
 */
public class TransacaoDto {

    private Long id;
    private Long usuarioId;
    private TipoTransacao tipo;
    private BigDecimal valorOriginal;
    private String moedaOriginal;
    private BigDecimal valorConvertido;
    private String moedaConvertida;
    private BigDecimal taxaCambioAplicada;
    private CategoriaTransacao categoria;
    private LocalDateTime data;
    private String descricao;

    public TransacaoDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public String getMoedaOriginal() {
        return moedaOriginal;
    }

    public void setMoedaOriginal(String moedaOriginal) {
        this.moedaOriginal = moedaOriginal;
    }

    public BigDecimal getValorConvertido() {
        return valorConvertido;
    }

    public void setValorConvertido(BigDecimal valorConvertido) {
        this.valorConvertido = valorConvertido;
    }

    public String getMoedaConvertida() {
        return moedaConvertida;
    }

    public void setMoedaConvertida(String moedaConvertida) {
        this.moedaConvertida = moedaConvertida;
    }

    public BigDecimal getTaxaCambioAplicada() {
        return taxaCambioAplicada;
    }

    public void setTaxaCambioAplicada(BigDecimal taxaCambioAplicada) {
        this.taxaCambioAplicada = taxaCambioAplicada;
    }

    public CategoriaTransacao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTransacao categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}




