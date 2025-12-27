package br.com.gestao.financeira.aplicacao.dto;

import java.time.LocalDateTime;

/**
 * DTO contendo os parâmetros para geração de relatórios.
 */
public class ParametrosRelatorio {

    private Long usuarioId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String moeda;
    private String categoria;

    public ParametrosRelatorio() {
    }

    public ParametrosRelatorio(Long usuarioId, LocalDateTime dataInicio,
            LocalDateTime dataFim, String moeda, String categoria) {
        this.usuarioId = usuarioId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.moeda = moeda;
        this.categoria = categoria;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}




