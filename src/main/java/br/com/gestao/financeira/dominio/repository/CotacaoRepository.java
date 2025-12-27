package br.com.gestao.financeira.dominio.repository;

import br.com.gestao.financeira.dominio.modelo.Moeda;
import br.com.gestao.financeira.dominio.modelo.TaxaCambio;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Port de saída para operações de câmbio.
 * Define o contrato para obtenção de moedas e taxas de câmbio.
 */
public interface CotacaoRepository {

    /**
     * Lista todas as moedas suportadas pelo sistema.
     * 
     * @return lista de moedas suportadas
     */
    List<Moeda> listarMoedasSuportadas();

    /**
     * Obtém a taxa de câmbio atual entre duas moedas.
     * 
     * @param origem  código da moeda de origem (ex: USD)
     * @param destino código da moeda de destino (ex: BRL)
     * @return Optional contendo a taxa de câmbio, se disponível
     */
    Optional<TaxaCambio> obterTaxaAtual(String origem, String destino);

    /**
     * Converte um valor de uma moeda para outra.
     * 
     * @param valor   o valor a converter
     * @param origem  código da moeda de origem
     * @param destino código da moeda de destino
     * @return o valor convertido
     */
    BigDecimal converterValor(BigDecimal valor, String origem, String destino);
}




