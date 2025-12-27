package br.com.gestao.financeira.infraestrutura.integrations;

import br.com.gestao.financeira.dominio.modelo.Moeda;
import br.com.gestao.financeira.dominio.modelo.TaxaCambio;
import br.com.gestao.financeira.dominio.repository.CotacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador HTTP para integração com a AwesomeAPI (economia.awesomeapi.com.br).
 * Implementa o CotacaoRepository para obter taxas de câmbio.
 * 
 * Possui retry automático para falhas de conexão (3 tentativas com backoff
 * exponencial).
 */
@Component
public class AwesomeApiCotacaoAdapter implements CotacaoRepository {

    private static final Logger log = LoggerFactory.getLogger(AwesomeApiCotacaoAdapter.class);
    private static final String AWESOME_API_URL = "https://economia.awesomeapi.com.br/last/";

    private final RestTemplate restTemplate;
    private final BrasilApiCambioAdapter brasilApiAdapter;

    public AwesomeApiCotacaoAdapter(RestTemplate restTemplate,
            BrasilApiCambioAdapter brasilApiAdapter) {
        this.restTemplate = restTemplate;
        this.brasilApiAdapter = brasilApiAdapter;
    }

    @Override
    public List<Moeda> listarMoedasSuportadas() {
        return brasilApiAdapter.listarMoedas();
    }

    @Override
    @Cacheable(value = "taxas", key = "#origem + '-' + #destino", unless = "#result == null")
    @Retryable(retryFor = {
            RestClientException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public Optional<TaxaCambio> obterTaxaAtual(String origem, String destino) {
        // Se as moedas são iguais, taxa é 1
        if (origem.equalsIgnoreCase(destino)) {
            return Optional.of(new TaxaCambio(origem, destino, BigDecimal.ONE, LocalDateTime.now()));
        }

        String par = origem + "-" + destino;
        String url = AWESOME_API_URL + par;
        log.info("Buscando taxa de câmbio: {} -> {}", origem, destino);

        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> response = restTemplate.getForObject(url, Map.class);

        if (response != null) {
            String chave = origem + destino;
            Map<String, String> cotacao = response.get(chave);

            if (cotacao != null && cotacao.containsKey("bid")) {
                BigDecimal taxa = new BigDecimal(cotacao.get("bid"));
                log.info("Taxa obtida: {} {} = {} {}", 1, origem, taxa, destino);
                return Optional.of(new TaxaCambio(origem, destino, taxa, LocalDateTime.now()));
            }
        }

        // Tenta conversão inversa
        return tentarConversaoInversa(origem, destino);
    }

    /**
     * Método de recuperação quando todas as tentativas de retry falham.
     */
    @Recover
    public Optional<TaxaCambio> recuperarFalhaTaxa(RestClientException e, String origem, String destino) {
        log.warn("Todas as tentativas falharam para {}-{}. Tentando conversão inversa. Erro: {}",
                origem, destino, e.getMessage());
        return tentarConversaoInversa(origem, destino);
    }

    @Override
    public BigDecimal converterValor(BigDecimal valor, String origem, String destino) {
        if (origem.equalsIgnoreCase(destino)) {
            return valor;
        }

        Optional<TaxaCambio> taxa = obterTaxaAtual(origem, destino);

        if (taxa.isPresent()) {
            return valor.multiply(taxa.get().getTaxa()).setScale(2, RoundingMode.HALF_UP);
        }

        log.warn("Taxa não disponível para {}-{}, retornando valor original", origem, destino);
        return valor;
    }

    /**
     * Tenta obter a taxa inversa quando a direta não está disponível.
     */
    private Optional<TaxaCambio> tentarConversaoInversa(String origem, String destino) {
        String parInverso = destino + "-" + origem;
        String url = AWESOME_API_URL + parInverso;

        log.info("Tentando conversão inversa: {} -> {}", destino, origem);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                String chave = destino + origem;
                Map<String, String> cotacao = response.get(chave);

                if (cotacao != null && cotacao.containsKey("bid")) {
                    BigDecimal taxaInversa = new BigDecimal(cotacao.get("bid"));
                    BigDecimal taxa = BigDecimal.ONE.divide(taxaInversa, 6, RoundingMode.HALF_UP);
                    log.info("Taxa inversa calculada: {} {} = {} {}", 1, origem, taxa, destino);
                    return Optional.of(new TaxaCambio(origem, destino, taxa, LocalDateTime.now()));
                }
            }
        } catch (Exception e) {
            log.error("Erro na conversão inversa {}-{}: {}", destino, origem, e.getMessage());
        }

        return Optional.empty();
    }
}
