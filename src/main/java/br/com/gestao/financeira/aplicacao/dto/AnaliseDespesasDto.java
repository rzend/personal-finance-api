package br.com.gestao.financeira.aplicacao.dto;

import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.enums.TipoTransacao;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO para resultado da análise de despesas.
 */
public class AnaliseDespesasDto {

    private Long usuarioId;
    private String periodo;
    private String moedaPadrao;
    private BigDecimal totalGeral;
    private BigDecimal ticketMedio;
    private int quantidadeTransacoes;
    private List<ResumoCategoria> resumoPorCategoria;
    private Map<String, BigDecimal> totalPorMes;

    // Investimentos separados das despesas
    private BigDecimal totalInvestimentos;
    private int quantidadeInvestimentos;
    private BigDecimal ticketMedioInvestimentos;
    private Map<String, BigDecimal> investimentosPorMes;

    // Ticket médio por tipo de transação (Despesa, Transferência, Retirada)
    private Map<TipoTransacao, BigDecimal> ticketMedioPorTipoTransacao;

    public AnaliseDespesasDto() {
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getMoedaPadrao() {
        return moedaPadrao;
    }

    public void setMoedaPadrao(String moedaPadrao) {
        this.moedaPadrao = moedaPadrao;
    }

    public BigDecimal getTotalGeral() {
        return totalGeral;
    }

    public void setTotalGeral(BigDecimal totalGeral) {
        this.totalGeral = totalGeral;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public int getQuantidadeTransacoes() {
        return quantidadeTransacoes;
    }

    public void setQuantidadeTransacoes(int quantidadeTransacoes) {
        this.quantidadeTransacoes = quantidadeTransacoes;
    }

    public List<ResumoCategoria> getResumoPorCategoria() {
        return resumoPorCategoria;
    }

    public void setResumoPorCategoria(List<ResumoCategoria> resumoPorCategoria) {
        this.resumoPorCategoria = resumoPorCategoria;
    }

    public Map<String, BigDecimal> getTotalPorMes() {
        return totalPorMes;
    }

    public void setTotalPorMes(Map<String, BigDecimal> totalPorMes) {
        this.totalPorMes = totalPorMes;
    }

    public BigDecimal getTotalInvestimentos() {
        return totalInvestimentos;
    }

    public void setTotalInvestimentos(BigDecimal totalInvestimentos) {
        this.totalInvestimentos = totalInvestimentos;
    }

    public int getQuantidadeInvestimentos() {
        return quantidadeInvestimentos;
    }

    public void setQuantidadeInvestimentos(int quantidadeInvestimentos) {
        this.quantidadeInvestimentos = quantidadeInvestimentos;
    }

    public BigDecimal getTicketMedioInvestimentos() {
        return ticketMedioInvestimentos;
    }

    public void setTicketMedioInvestimentos(BigDecimal ticketMedioInvestimentos) {
        this.ticketMedioInvestimentos = ticketMedioInvestimentos;
    }

    public Map<String, BigDecimal> getInvestimentosPorMes() {
        return investimentosPorMes;
    }

    public void setInvestimentosPorMes(Map<String, BigDecimal> investimentosPorMes) {
        this.investimentosPorMes = investimentosPorMes;
    }

    public Map<TipoTransacao, BigDecimal> getTicketMedioPorTipoTransacao() {
        return ticketMedioPorTipoTransacao;
    }

    public void setTicketMedioPorTipoTransacao(Map<TipoTransacao, BigDecimal> ticketMedioPorTipoTransacao) {
        this.ticketMedioPorTipoTransacao = ticketMedioPorTipoTransacao;
    }

    /**
     * Classe interna para resumo por categoria.
     */
    public static class ResumoCategoria {
        private CategoriaTransacao categoria;
        private BigDecimal total;
        private BigDecimal percentual;
        private int quantidade;
        private BigDecimal ticketMedio;

        public ResumoCategoria() {
        }

        public ResumoCategoria(CategoriaTransacao categoria, BigDecimal total,
                BigDecimal percentual, int quantidade, BigDecimal ticketMedio) {
            this.categoria = categoria;
            this.total = total;
            this.percentual = percentual;
            this.quantidade = quantidade;
            this.ticketMedio = ticketMedio;
        }

        public CategoriaTransacao getCategoria() {
            return categoria;
        }

        public void setCategoria(CategoriaTransacao categoria) {
            this.categoria = categoria;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public BigDecimal getPercentual() {
            return percentual;
        }

        public void setPercentual(BigDecimal percentual) {
            this.percentual = percentual;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public BigDecimal getTicketMedio() {
            return ticketMedio;
        }

        public void setTicketMedio(BigDecimal ticketMedio) {
            this.ticketMedio = ticketMedio;
        }
    }
}
