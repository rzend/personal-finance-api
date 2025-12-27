package br.com.gestao.financeira.aplicacao.dto;

import java.util.List;

public class ResultadoImportacaoDto {
    private int totalProcessados;
    private int sucessos;
    private int falhas;
    private List<String> erros;

    public ResultadoImportacaoDto(int totalProcessados, int sucessos, int falhas, List<String> erros) {
        this.totalProcessados = totalProcessados;
        this.sucessos = sucessos;
        this.falhas = falhas;
        this.erros = erros;
    }

    public int getTotalProcessados() {
        return totalProcessados;
    }

    public void setTotalProcessados(int totalProcessados) {
        this.totalProcessados = totalProcessados;
    }

    public int getSucessos() {
        return sucessos;
    }

    public void setSucessos(int sucessos) {
        this.sucessos = sucessos;
    }

    public int getFalhas() {
        return falhas;
    }

    public void setFalhas(int falhas) {
        this.falhas = falhas;
    }

    public List<String> getErros() {
        return erros;
    }

    public void setErros(List<String> erros) {
        this.erros = erros;
    }
}
