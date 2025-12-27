package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.aplicacao.dto.ConversaoDto;
import br.com.gestao.financeira.aplicacao.dto.TaxaCambioDto;
import br.com.gestao.financeira.dominio.modelo.Moeda;
import br.com.gestao.financeira.dominio.modelo.TaxaCambio;
import br.com.gestao.financeira.dominio.repository.CotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CambioService Tests")
class CambioServiceTest {

    @Mock
    private CotacaoRepository cotacaoRepository;

    @InjectMocks
    private CambioService cambioService;

    private TaxaCambio taxaCambio;

    @BeforeEach
    void setUp() {
        taxaCambio = new TaxaCambio("USD", "BRL", new BigDecimal("5.50"), LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve calcular custo de câmbio corretamente")
    void deveCalcularCustoCambioCorretamente() {
        when(cotacaoRepository.obterTaxaAtual("USD", "BRL")).thenReturn(Optional.of(taxaCambio));

        ConversaoDto resultado = cambioService.calcularCustoDoCambio(
                new BigDecimal("100"),
                "USD",
                "BRL",
                new BigDecimal("0.02"));

        assertNotNull(resultado);
        assertEquals(new BigDecimal("550.00"), resultado.getValorConvertido());
        assertEquals(new BigDecimal("2.00"), resultado.getMargemAplicada());
        assertEquals(new BigDecimal("552.00"), resultado.getCustoTotal());
    }

    @Test
    @DisplayName("Deve obter taxa de câmbio com sucesso")
    void deveObterTaxaCambioComSucesso() {
        when(cotacaoRepository.obterTaxaAtual("USD", "BRL")).thenReturn(Optional.of(taxaCambio));

        TaxaCambio resultado = cambioService.obterTaxaCambio("USD", "BRL");

        assertNotNull(resultado);
        assertEquals(new BigDecimal("5.50"), resultado.getTaxa());
    }

    @Test
    @DisplayName("Deve lançar exceção para moeda não suportada")
    void deveLancarExcecaoParaMoedaNaoSuportada() {
        when(cotacaoRepository.obterTaxaAtual("XXX", "YYY")).thenReturn(Optional.empty());

        assertThrows(CambioService.MoedaNaoSuportadaException.class,
                () -> cambioService.obterTaxaCambio("XXX", "YYY"));
    }

    @Test
    @DisplayName("Deve obter taxa como DTO")
    void deveObterTaxaComoDto() {
        when(cotacaoRepository.obterTaxaAtual("USD", "BRL")).thenReturn(Optional.of(taxaCambio));

        TaxaCambioDto resultado = cambioService.obterTaxaAtualDto("USD", "BRL");

        assertNotNull(resultado);
        assertEquals("USD", resultado.getMoedaOrigem());
        assertEquals("BRL", resultado.getMoedaDestino());
    }

    @Test
    @DisplayName("Deve listar moedas suportadas")
    void deveListarMoedasSuportadas() {
        List<Moeda> moedas = List.of(
                new Moeda("USD", "Dólar", "$"),
                new Moeda("BRL", "Real", "R$"));
        when(cotacaoRepository.listarMoedasSuportadas()).thenReturn(moedas);

        List<Moeda> resultado = cambioService.listarMoedasSuportadas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve converter valor para mesma moeda")
    void deveConverterValorParaMesmaMoeda() {
        BigDecimal valor = new BigDecimal("100");

        BigDecimal resultado = cambioService.converterValor(valor, "BRL", "BRL");

        assertEquals(valor, resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção para valor inválido")
    void deveLancarExcecaoParaValorInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> cambioService.calcularCustoDoCambio(
                        BigDecimal.ZERO, "USD", "BRL", BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Deve lançar exceção para margem negativa")
    void deveLancarExcecaoParaMargemNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> cambioService.calcularCustoDoCambio(
                        new BigDecimal("100"), "USD", "BRL", new BigDecimal("-0.01")));
    }
}
