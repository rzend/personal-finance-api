package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.aplicacao.dto.AnaliseDespesasDto;
import br.com.gestao.financeira.aplicacao.dto.AnaliseDespesasDto.ResumoCategoria;
import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.enums.TipoTransacao;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.repository.CotacaoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço de domínio responsável pela análise de despesas.
 */
@Service
public class AnaliseDespesasService {

    private final br.com.gestao.financeira.dominio.services.TransacaoService transacaoService;
    private final CotacaoRepository cambioPort;

    public AnaliseDespesasService(br.com.gestao.financeira.dominio.services.TransacaoService transacaoService,
            CotacaoRepository cambioPort) {
        this.transacaoService = transacaoService;
        this.cambioPort = cambioPort;
    }

    /**
     * Analisa as despesas de um usuário em um período.
     * 
     * @param usuarioId   identificador do usuário
     * @param dataInicio  início do período
     * @param dataFim     fim do período
     * @param moedaPadrao moeda para normalização dos valores
     * @return DTO com análise completa das despesas
     */
    public AnaliseDespesasDto analisar(Long usuarioId, LocalDateTime dataInicio,
            LocalDateTime dataFim, String moedaPadrao) {
        List<Transacao> transacoes = transacaoService.listarTransacoes(
                usuarioId, dataInicio, dataFim, null, null, Pageable.unpaged()).getContent();

        // Filtra despesas (retiradas, transferências e despesas gerais), EXCLUINDO
        // investimentos
        List<Transacao> despesas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RETIRADA ||
                        t.getTipo() == TipoTransacao.TRANSFERENCIA ||
                        t.getTipo() == TipoTransacao.DESPESA)
                .filter(t -> t.getCategoria() != CategoriaTransacao.INVESTIMENTOS)
                .collect(Collectors.toList());

        // Filtra apenas investimentos (são saídas mas tratadas separadamente)
        List<Transacao> investimentos = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RETIRADA ||
                        t.getTipo() == TipoTransacao.TRANSFERENCIA ||
                        t.getTipo() == TipoTransacao.DESPESA)
                .filter(t -> t.getCategoria() == CategoriaTransacao.INVESTIMENTOS)
                .collect(Collectors.toList());

        AnaliseDespesasDto analise = new AnaliseDespesasDto();
        analise.setUsuarioId(usuarioId);
        analise.setPeriodo(formatarPeriodo(dataInicio, dataFim));
        analise.setMoedaPadrao(moedaPadrao);
        analise.setQuantidadeTransacoes(despesas.size());

        // Calcula totais de despesas normalizando para moeda padrão
        BigDecimal totalGeral = calcularTotalNormalizado(despesas, moedaPadrao);
        analise.setTotalGeral(totalGeral);

        // Calcula ticket médio geral das despesas
        if (!despesas.isEmpty()) {
            analise.setTicketMedio(totalGeral.divide(
                    BigDecimal.valueOf(despesas.size()), 2, RoundingMode.HALF_UP));
        } else {
            analise.setTicketMedio(BigDecimal.ZERO);
        }

        // Resumo por categoria (com ticket médio por categoria)
        analise.setResumoPorCategoria(calcularResumoPorCategoria(despesas, moedaPadrao, totalGeral));

        // Total por mês (apenas despesas, sem investimentos)
        analise.setTotalPorMes(calcularTotalPorMes(despesas, moedaPadrao));

        // Ticket médio por tipo de transação (Despesa, Retirada, Transferência)
        analise.setTicketMedioPorTipoTransacao(calcularTicketMedioPorTipo(despesas, moedaPadrao));

        // === INVESTIMENTOS (separados das despesas) ===
        BigDecimal totalInvestimentos = calcularTotalNormalizado(investimentos, moedaPadrao);
        analise.setTotalInvestimentos(totalInvestimentos);
        analise.setQuantidadeInvestimentos(investimentos.size());

        if (!investimentos.isEmpty()) {
            analise.setTicketMedioInvestimentos(totalInvestimentos.divide(
                    BigDecimal.valueOf(investimentos.size()), 2, RoundingMode.HALF_UP));
        } else {
            analise.setTicketMedioInvestimentos(BigDecimal.ZERO);
        }

        // Investimentos por mês
        analise.setInvestimentosPorMes(calcularTotalPorMes(investimentos, moedaPadrao));

        return analise;
    }

    private BigDecimal calcularTotalNormalizado(List<Transacao> transacoes, String moedaPadrao) {
        return transacoes.stream()
                .map(t -> normalizarValor(t.getValorOriginal(), t.getMoedaOriginal(), moedaPadrao))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizarValor(BigDecimal valor, String moedaOriginal, String moedaPadrao) {
        if (moedaOriginal.equalsIgnoreCase(moedaPadrao)) {
            return valor;
        }
        return cambioPort.converterValor(valor, moedaOriginal, moedaPadrao);
    }

    private List<ResumoCategoria> calcularResumoPorCategoria(List<Transacao> transacoes,
            String moedaPadrao,
            BigDecimal totalGeral) {
        Map<CategoriaTransacao, List<Transacao>> porCategoria = transacoes.stream()
                .collect(Collectors.groupingBy(Transacao::getCategoria));

        List<ResumoCategoria> resumos = new ArrayList<>();

        for (Map.Entry<CategoriaTransacao, List<Transacao>> entry : porCategoria.entrySet()) {
            CategoriaTransacao categoria = entry.getKey();
            List<Transacao> transacoesCategoria = entry.getValue();

            BigDecimal totalCategoria = calcularTotalNormalizado(transacoesCategoria, moedaPadrao);
            BigDecimal percentual = BigDecimal.ZERO;

            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                percentual = totalCategoria
                        .divide(totalGeral, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            // Calcula ticket médio por categoria
            BigDecimal ticketMedioCategoria = BigDecimal.ZERO;
            if (!transacoesCategoria.isEmpty()) {
                ticketMedioCategoria = totalCategoria.divide(
                        BigDecimal.valueOf(transacoesCategoria.size()), 2, RoundingMode.HALF_UP);
            }

            resumos.add(new ResumoCategoria(
                    categoria,
                    totalCategoria,
                    percentual,
                    transacoesCategoria.size(),
                    ticketMedioCategoria));
        }

        // Ordena por total decrescente
        resumos.sort((a, b) -> b.getTotal().compareTo(a.getTotal()));

        return resumos;
    }

    private Map<String, BigDecimal> calcularTotalPorMes(List<Transacao> transacoes, String moedaPadrao) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return transacoes.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getData().format(formatter),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                t -> normalizarValor(t.getValorOriginal(), t.getMoedaOriginal(), moedaPadrao),
                                BigDecimal::add)));
    }

    private Map<TipoTransacao, BigDecimal> calcularTicketMedioPorTipo(List<Transacao> transacoes, String moedaPadrao) {
        Map<TipoTransacao, List<Transacao>> porTipo = transacoes.stream()
                .collect(Collectors.groupingBy(Transacao::getTipo));

        Map<TipoTransacao, BigDecimal> ticketMedios = new HashMap<>();

        for (Map.Entry<TipoTransacao, List<Transacao>> entry : porTipo.entrySet()) {
            BigDecimal totalTipo = calcularTotalNormalizado(entry.getValue(), moedaPadrao);
            BigDecimal ticketMedio = totalTipo.divide(
                    BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP);
            ticketMedios.put(entry.getKey(), ticketMedio);
        }

        return ticketMedios;
    }

    private String formatarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String inicioStr = inicio != null ? inicio.format(formatter) : "início";
        String fimStr = fim != null ? fim.format(formatter) : "hoje";
        return inicioStr + " a " + fimStr;
    }
}
