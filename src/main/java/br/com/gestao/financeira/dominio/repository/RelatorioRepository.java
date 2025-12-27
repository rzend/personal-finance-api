package br.com.gestao.financeira.dominio.repository;

import br.com.gestao.financeira.aplicacao.dto.ParametrosRelatorio;

/**
 * Port de saída para geração de relatórios.
 * Define o contrato para geração de relatórios em PDF e Excel.
 */
public interface RelatorioRepository {

    /**
     * Gera um relatório de transações em formato PDF.
     * 
     * @param parametros os parâmetros para geração do relatório
     * @return array de bytes contendo o arquivo PDF
     */
    byte[] gerarRelatorioPDF(ParametrosRelatorio parametros);

    /**
     * Gera um relatório de transações em formato Excel.
     * 
     * @param parametros os parâmetros para geração do relatório
     * @return array de bytes contendo o arquivo Excel
     */
    byte[] gerarRelatorioExcel(ParametrosRelatorio parametros);
}




