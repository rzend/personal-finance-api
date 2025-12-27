package br.com.gestao.financeira.infraestrutura.integrations;

import br.com.gestao.financeira.dominio.modelo.Moeda;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador HTTP para integração com a BrasilAPI.
 * Obtém a lista de moedas suportadas.
 * 
 * Possui retry automático para falhas de conexão (3 tentativas com backoff
 * exponencial).
 */
@Component
public class BrasilApiCambioAdapter {

    private static final Logger log = LoggerFactory.getLogger(BrasilApiCambioAdapter.class);

    private final RestTemplate restTemplate;
    private final String brasilApiUrl;

    public BrasilApiCambioAdapter(RestTemplate restTemplate,
            @Value("${apis.brasilapi.url:https://brasilapi.com.br}") String brasilApiUrl) {
        this.restTemplate = restTemplate;
        this.brasilApiUrl = brasilApiUrl;
    }

    /**
     * Lista todas as moedas suportadas pela BrasilAPI.
     * O resultado é cacheado para evitar chamadas repetidas.
     * Possui retry automático com 3 tentativas.
     * 
     * @return lista de moedas disponíveis
     */
    @Cacheable(value = "moedas", unless = "#result.isEmpty()")
    @Retryable(retryFor = {
            RestClientException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public List<Moeda> listarMoedas() {
        String url = brasilApiUrl + "/api/cambio/v1/moedas";
        log.info("Buscando moedas na BrasilAPI: {}", url);

        MoedaResponse[] response = restTemplate.getForObject(url, MoedaResponse[].class);

        if (response != null) {
            List<Moeda> moedas = Arrays.stream(response)
                    .map(r -> {
                        Moeda m = new Moeda(r.simbolo, r.nome);
                        m.setTipoMoeda(r.tipoMoeda);
                        return m;
                    })
                    .collect(Collectors.toList());

            // Adiciona BRL que não vem na lista da BrasilAPI
            Moeda brl = new Moeda("BRL", "Real brasileiro");
            brl.setTipoMoeda("A");
            moedas.add(brl);

            log.info("Obtidas {} moedas da BrasilAPI", moedas.size());
            return moedas;
        }

        return getMoedasFallback();
    }

    /**
     * Método de recuperação quando todas as tentativas de retry falham.
     */
    @Recover
    public List<Moeda> recuperarFalhaMoedas(RestClientException e) {
        log.warn("Todas as tentativas falharam ao buscar moedas. Usando fallback. Erro: {}", e.getMessage());
        return getMoedasFallback();
    }

    private List<Moeda> getMoedasFallback() {
        log.warn("Usando lista de moedas fallback");
        List<Moeda> fallback = new ArrayList<>();
        fallback.add(criarMoeda("BRL", "Real brasileiro", "A"));
        fallback.add(criarMoeda("USD", "Dólar dos Estados Unidos", "A"));
        fallback.add(criarMoeda("EUR", "Euro", "B"));
        fallback.add(criarMoeda("GBP", "Libra Esterlina", "B"));
        fallback.add(criarMoeda("JPY", "Iene", "A"));
        fallback.add(criarMoeda("AUD", "Dólar australiano", "B"));
        fallback.add(criarMoeda("CAD", "Dólar canadense", "A"));
        fallback.add(criarMoeda("CHF", "Franco suíço", "A"));
        return fallback;
    }

    private Moeda criarMoeda(String codigo, String nome, String tipoMoeda) {
        Moeda m = new Moeda(codigo, nome);
        m.setTipoMoeda(tipoMoeda);
        return m;
    }

    /**
     * DTO interno para deserialização da resposta da BrasilAPI.
     */
    private static class MoedaResponse {
        @JsonProperty("simbolo")
        String simbolo;

        @JsonProperty("nome")
        String nome;

        @JsonProperty("tipo_moeda")
        String tipoMoeda;
    }
}
