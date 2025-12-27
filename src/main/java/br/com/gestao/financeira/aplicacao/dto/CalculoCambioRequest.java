package br.com.gestao.financeira.aplicacao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * DTO para requisição de cálculo de custo de câmbio.
 */
public class CalculoCambioRequest {

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotBlank(message = "A moeda de origem é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}$", message = "A moeda de origem deve ser um código de 3 letras (ex: USD)")
    private String moedaOrigem;

    @NotBlank(message = "A moeda de destino é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}$", message = "A moeda de destino deve ser um código de 3 letras (ex: BRL)")
    private String moedaDestino;

    @NotNull(message = "A margem é obrigatória")
    @DecimalMin(value = "0", message = "A margem não pode ser negativa")
    private BigDecimal margem;

    public CalculoCambioRequest() {
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public BigDecimal getMargem() {
        return margem;
    }

    public void setMargem(BigDecimal margem) {
        this.margem = margem;
    }
}




