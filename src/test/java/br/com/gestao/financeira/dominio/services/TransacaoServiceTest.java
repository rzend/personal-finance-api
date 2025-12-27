package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.enums.TipoTransacao;
import br.com.gestao.financeira.dominio.repository.TransacaoRepository;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransacaoService Tests")
@SuppressWarnings("null")
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Transacao transacao;

    @BeforeEach
    void setUp() {
        transacao = new Transacao();
        transacao.setId(1L);
        transacao.setUsuarioId(1L);
        transacao.setTipo(TipoTransacao.DESPESA);
        transacao.setValorOriginal(new BigDecimal("100.00"));
        transacao.setMoedaOriginal("BRL");
        transacao.setCategoria(CategoriaTransacao.ALIMENTACAO);
        transacao.setDescricao("Almoço");
        transacao.setData(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve registrar transação com sucesso")
    void deveRegistrarTransacaoComSucesso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);

        Transacao resultado = transacaoService.registrarTransacao(transacao);

        assertNotNull(resultado);
        assertEquals(TipoTransacao.DESPESA, resultado.getTipo());
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar transação para usuário inexistente")
    void deveLancarExcecaoAoRegistrarTransacaoParaUsuarioInexistente() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(UsuarioService.UsuarioNaoEncontradoException.class,
                () -> transacaoService.registrarTransacao(transacao));
    }

    @Test
    @DisplayName("Deve definir data atual quando não informada")
    void deveDefinirDataAtualQuandoNaoInformada() {
        transacao.setData(null);
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArgument(0));

        Transacao resultado = transacaoService.registrarTransacao(transacao);

        assertNotNull(resultado.getData());
    }

    @Test
    @DisplayName("Deve definir moeda BRL quando não informada")
    void deveDefinirMoedaBrlQuandoNaoInformada() {
        transacao.setMoedaOriginal(null);
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArgument(0));

        Transacao resultado = transacaoService.registrarTransacao(transacao);

        assertEquals("BRL", resultado.getMoedaOriginal());
    }

    @Test
    @DisplayName("Deve atualizar transação com sucesso")
    void deveAtualizarTransacaoComSucesso() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);

        Transacao atualizacao = new Transacao();
        atualizacao.setValorOriginal(new BigDecimal("150.00"));
        atualizacao.setDescricao("Jantar");

        Transacao resultado = transacaoService.atualizarTransacao(1L, atualizacao);

        assertNotNull(resultado);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar transação inexistente")
    void deveLancarExcecaoAoAtualizarTransacaoInexistente() {
        when(transacaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TransacaoService.TransacaoNaoEncontradaException.class,
                () -> transacaoService.atualizarTransacao(999L, new Transacao()));
    }

    @Test
    @DisplayName("Deve excluir transação com sucesso")
    void deveExcluirTransacaoComSucesso() {
        when(transacaoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transacaoRepository).deleteById(1L);

        assertDoesNotThrow(() -> transacaoService.excluirTransacao(1L));
        verify(transacaoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir transação inexistente")
    void deveLancarExcecaoAoExcluirTransacaoInexistente() {
        when(transacaoRepository.existsById(999L)).thenReturn(false);

        assertThrows(TransacaoService.TransacaoNaoEncontradaException.class,
                () -> transacaoService.excluirTransacao(999L));
    }

    @Test
    @DisplayName("Deve detalhar transação com sucesso")
    void deveDetalharTransacaoComSucesso() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        Transacao resultado = transacaoService.detalharTransacao(1L);

        assertNotNull(resultado);
        assertEquals("Almoço", resultado.getDescricao());
    }

    @Test
    @DisplayName("Deve listar transações com paginação")
    @SuppressWarnings("unchecked")
    void deveListarTransacoesComPaginacao() {
        Page<Transacao> page = new PageImpl<>(List.of(transacao));
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Transacao> resultado = transacaoService.listarTransacoes(
                1L, null, null, null, null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }
}
