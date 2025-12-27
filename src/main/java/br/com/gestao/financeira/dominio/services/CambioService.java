package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.aplicacao.dto.ConversaoDto;
import br.com.gestao.financeira.aplicacao.dto.TaxaCambioDto;
import br.com.gestao.financeira.dominio.modelo.Moeda;
import br.com.gestao.financeira.dominio.modelo.TaxaCambio;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.repository.CotacaoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Serviço de domínio responsável por operações de câmbio.
 */
@Service
public class CambioService {

    private final CotacaoRepository cambioPort;

    public CambioService(CotacaoRepository cambioPort) {
        this.cambioPort = cambioPort;
    }

    /**
     * Calcula o custo total de uma operação de câmbio.
     * Fórmula: custoTotal = (valor × taxa) + margemAplicada
     * onde margemAplicada = valor × margem
     * 
     * @param valor        valor a converter
     * @param moedaOrigem  código da moeda de origem
     * @param moedaDestino código da moeda de destino
     * @param margem       margem/spread a aplicar (ex: 0.02 para 2%)
     * @return DTO com detalhes da conversão e custos
     */
    public ConversaoDto calcularCustoDoCambio(BigDecimal valor, String moedaOrigem,
            String moedaDestino, BigDecimal margem) {
        validarParametros(valor, moedaOrigem, moedaDestino, margem);

        TaxaCambio taxaCambio = obterTaxaCambio(moedaOrigem, moedaDestino);
        BigDecimal taxa = taxaCambio.getTaxa();

        // Cálculos
        BigDecimal valorConvertido = valor.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
        BigDecimal margemAplicada = valor.multiply(margem).setScale(2, RoundingMode.HALF_UP);
        BigDecimal custoTotal = valorConvertido.add(margemAplicada).setScale(2, RoundingMode.HALF_UP);

        return new ConversaoDto(
                valor,
                moedaOrigem,
                valorConvertido,
                moedaDestino,
                taxa,
                margemAplicada,
                custoTotal);
    }

    /**
     * Obtém a taxa de câmbio atual entre duas moedas.
     * 
     * @param origem  código da moeda de origem
     * @param destino código da moeda de destino
     * @return a taxa de câmbio
     * @throws MoedaNaoSuportadaException se a conversão não é suportada
     */
    @Cacheable(value = "taxasCambio", key = "#origem + '-' + #destino")
    public TaxaCambio obterTaxaCambio(String origem, String destino) {
        return cambioPort.obterTaxaAtual(origem, destino)
                .orElseThrow(() -> new MoedaNaoSuportadaException(origem, destino));
    }

    /**
     * Obtém a taxa de câmbio atual como DTO.
     */
    public TaxaCambioDto obterTaxaAtualDto(String origem, String destino) {
        TaxaCambio taxa = obterTaxaCambio(origem, destino);
        return new TaxaCambioDto(
                taxa.getMoedaOrigem(),
                taxa.getMoedaDestino(),
                taxa.getTaxa(),
                taxa.getObtidaEm());
    }

    /**
     * Converte um valor de uma moeda para outra.
     * 
     * @param valor   valor a converter
     * @param origem  código da moeda de origem
     * @param destino código da moeda de destino
     * @return valor convertido
     */
    public BigDecimal converterValor(BigDecimal valor, String origem, String destino) {
        if (origem.equalsIgnoreCase(destino)) {
            return valor;
        }
        return cambioPort.converterValor(valor, origem, destino);
    }

    /**
     * Converte uma transação para a moeda de destino.
     * 
     * @param transacao    a transação a converter
     * @param moedaDestino código da moeda de destino
     * @return DTO com valores convertidos
     */
    public ConversaoDto converterTransacao(Transacao transacao, String moedaDestino) {
        return calcularCustoDoCambio(
                transacao.getValorOriginal(),
                transacao.getMoedaOriginal(),
                moedaDestino,
                BigDecimal.ZERO // Sem margem para visualização
        );
    }

    /**
     * Lista todas as moedas suportadas pelo sistema.
     * 
     * @return lista de moedas disponíveis
     */
    @Cacheable(value = "moedas")
    public List<Moeda> listarMoedasSuportadas() {
        return cambioPort.listarMoedasSuportadas();
    }

    private void validarParametros(BigDecimal valor, String moedaOrigem,
            String moedaDestino, BigDecimal margem) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        if (moedaOrigem == null || moedaOrigem.isBlank()) {
            throw new IllegalArgumentException("A moeda de origem é obrigatória");
        }
        if (moedaDestino == null || moedaDestino.isBlank()) {
            throw new IllegalArgumentException("A moeda de destino é obrigatória");
        }
        if (margem == null || margem.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A margem não pode ser negativa");
        }
    }

    public static class MoedaNaoSuportadaException extends RuntimeException {
        public MoedaNaoSuportadaException(String origem, String destino) {
            super("Conversão de " + origem + " para " + destino + " não suportada");
        }
    }
}
