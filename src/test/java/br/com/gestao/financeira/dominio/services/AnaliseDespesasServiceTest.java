package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.aplicacao.dto.AnaliseDespesasDto;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.enums.TipoTransacao;
import br.com.gestao.financeira.dominio.repository.CotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnaliseDespesasServiceTest {

        @Mock
        private TransacaoService transacaoService;

        @Mock
        private CotacaoRepository cambioPort;

        @InjectMocks
        private AnaliseDespesasService analiseDespesasService;

        private Transacao transacaoDespesa;
        private Transacao transacaoInvestimento;

        @BeforeEach
        void setUp() {
                transacaoDespesa = new Transacao();
                transacaoDespesa.setTipo(TipoTransacao.DESPESA);
                transacaoDespesa.setCategoria(CategoriaTransacao.ALIMENTACAO);
                transacaoDespesa.setValorOriginal(new BigDecimal("100.00"));
                transacaoDespesa.setMoedaOriginal("BRL");
                transacaoDespesa.setData(LocalDateTime.now());

                transacaoInvestimento = new Transacao();
                transacaoInvestimento.setTipo(TipoTransacao.DESPESA); // Pode ser marcado como DESPESA ou TRANSFERENCIA
                transacaoInvestimento.setCategoria(CategoriaTransacao.INVESTIMENTOS);
                transacaoInvestimento.setValorOriginal(new BigDecimal("500.00"));
                transacaoInvestimento.setMoedaOriginal("BRL");
                transacaoInvestimento.setData(LocalDateTime.now());
        }

        @Test
        void deveSepararInvestimentosDeDespesas() {
                when(transacaoService.listarTransacoes(anyLong(), any(), any(), any(), any(), any()))
                                .thenReturn(new PageImpl<>(List.of(transacaoDespesa, transacaoInvestimento)));

                // Mock conversão de moeda (1 pra 1 para simplificar)
                org.mockito.Mockito.lenient().when(cambioPort.converterValor(any(), anyString(), anyString()))
                                .thenAnswer(i -> i.getArgument(0));

                AnaliseDespesasDto resultado = analiseDespesasService.analisar(1L, LocalDateTime.now(),
                                LocalDateTime.now(),
                                "BRL");

                // Verifica Despesas
                assertEquals(new BigDecimal("100.00"), resultado.getTotalGeral());
                assertEquals(1, resultado.getQuantidadeTransacoes());

                // Verifica Investimentos
                assertEquals(new BigDecimal("500.00"), resultado.getTotalInvestimentos());
                assertEquals(1, resultado.getQuantidadeInvestimentos());

                // Verifica se investimento NÃO está no resumo de categorias de despesas
                assertTrue(resultado.getResumoPorCategoria().stream()
                                .noneMatch(r -> r.getCategoria() == CategoriaTransacao.INVESTIMENTOS));
        }

        @Test
        void deveCalcularTicketMedioCorretamente() {
                Transacao despesa2 = new Transacao();
                despesa2.setTipo(TipoTransacao.DESPESA);
                despesa2.setCategoria(CategoriaTransacao.ALIMENTACAO);
                despesa2.setValorOriginal(new BigDecimal("200.00"));
                despesa2.setMoedaOriginal("BRL");
                despesa2.setData(LocalDateTime.now());

                when(transacaoService.listarTransacoes(anyLong(), any(), any(), any(), any(), any()))
                                .thenReturn(new PageImpl<>(List.of(transacaoDespesa, despesa2)));

                org.mockito.Mockito.lenient().when(cambioPort.converterValor(any(), anyString(), anyString()))
                                .thenAnswer(i -> i.getArgument(0));

                AnaliseDespesasDto resultado = analiseDespesasService.analisar(1L, LocalDateTime.now(),
                                LocalDateTime.now(),
                                "BRL");

                // Total 300, qtd 2 -> ticket médio 150
                assertEquals(new BigDecimal("150.00"), resultado.getTicketMedio());

                // Ticket médio da categoria Alimentação
                assertEquals(new BigDecimal("150.00"), resultado.getResumoPorCategoria().get(0).getTicketMedio());
        }

        @Test
        void deveCalcularTicketMedioPorTipoTransacao() {
                Transacao ret = new Transacao();
                ret.setTipo(TipoTransacao.RETIRADA);
                ret.setCategoria(CategoriaTransacao.OUTROS);
                ret.setValorOriginal(new BigDecimal("50.00"));
                ret.setMoedaOriginal("BRL");
                ret.setData(LocalDateTime.now());

                Transacao transf = new Transacao();
                transf.setTipo(TipoTransacao.TRANSFERENCIA);
                transf.setCategoria(CategoriaTransacao.OUTROS);
                transf.setValorOriginal(new BigDecimal("200.00"));
                transf.setMoedaOriginal("BRL");
                transf.setData(LocalDateTime.now());

                when(transacaoService.listarTransacoes(anyLong(), any(), any(), any(), any(), any()))
                                .thenReturn(new PageImpl<>(List.of(transacaoDespesa, ret, transf)));

                org.mockito.Mockito.lenient().when(cambioPort.converterValor(any(), anyString(), anyString()))
                                .thenAnswer(i -> i.getArgument(0));

                AnaliseDespesasDto resultado = analiseDespesasService.analisar(1L, LocalDateTime.now(),
                                LocalDateTime.now(), "BRL");

                // Valida breakdown
                assertEquals(new BigDecimal("100.00"),
                                resultado.getTicketMedioPorTipoTransacao().get(TipoTransacao.DESPESA));
                assertEquals(new BigDecimal("50.00"),
                                resultado.getTicketMedioPorTipoTransacao().get(TipoTransacao.RETIRADA));
                assertEquals(new BigDecimal("200.00"),
                                resultado.getTicketMedioPorTipoTransacao().get(TipoTransacao.TRANSFERENCIA));
        }
}
