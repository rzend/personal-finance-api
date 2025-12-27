package br.com.gestao.financeira.dominio.entity;

import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.enums.TipoTransacao;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA para persistência de transações.
 */
@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(name = "valor_original", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorOriginal;

    @Column(name = "moeda_original", nullable = false, length = 3)
    private String moedaOriginal = "BRL";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaTransacao categoria;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    @Column(length = 500)
    private String descricao;

    @Column(name = "taxa_cambio_aplicada", precision = 19, scale = 6)
    private BigDecimal taxaCambioAplicada;

    public Transacao() {
    }

    // Getters e Setters
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

    public BigDecimal getTaxaCambioAplicada() {
        return taxaCambioAplicada;
    }

    public void setTaxaCambioAplicada(BigDecimal taxaCambioAplicada) {
        this.taxaCambioAplicada = taxaCambioAplicada;
    }
}






