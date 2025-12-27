package br.com.gestao.financeira.aplicacao.dto;

import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.enums.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * DTO para criação de transação.
 */
public class TransacaoCriacaoDto {

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "O tipo de transação é obrigatório")
    private TipoTransacao tipo;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotBlank(message = "A moeda é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}$", message = "A moeda deve ser um código de 3 letras (ex: BRL)")
    private String moeda;

    @NotNull(message = "A categoria é obrigatória")
    private CategoriaTransacao categoria;

    private String descricao;

    public TransacaoCriacaoDto() {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public CategoriaTransacao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTransacao categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}




