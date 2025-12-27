package br.com.gestao.financeira.infraestrutura.config;

import br.com.gestao.financeira.dominio.services.CambioService;
import br.com.gestao.financeira.dominio.services.TransacaoService;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ExcecaoHandler Tests")
@SuppressWarnings("null")
class ExcecaoHandlerTest {

    private final ExcecaoHandler excecaoHandler = new ExcecaoHandler();

    @Test
    @DisplayName("Deve retornar 404 para usuário não encontrado")
    void deveRetornar404ParaUsuarioNaoEncontrado() {
        UsuarioService.UsuarioNaoEncontradoException ex = new UsuarioService.UsuarioNaoEncontradoException(1L);

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleUsuarioNaoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 409 para email já cadastrado")
    void deveRetornar409ParaEmailJaCadastrado() {
        UsuarioService.EmailJaCadastradoException ex = new UsuarioService.EmailJaCadastradoException("teste@email.com");

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleEmailJaCadastrado(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 409 para CPF já cadastrado")
    void deveRetornar409ParaCpfJaCadastrado() {
        UsuarioService.CpfJaCadastradoException ex = new UsuarioService.CpfJaCadastradoException("12345678901");

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleCpfJaCadastrado(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 404 para transação não encontrada")
    void deveRetornar404ParaTransacaoNaoEncontrada() {
        TransacaoService.TransacaoNaoEncontradaException ex = new TransacaoService.TransacaoNaoEncontradaException(1L);

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleTransacaoNaoEncontrada(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 400 para moeda não suportada")
    void deveRetornar400ParaMoedaNaoSuportada() {
        CambioService.MoedaNaoSuportadaException ex = new CambioService.MoedaNaoSuportadaException("XXX", "YYY");

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleMoedaNaoSuportada(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 401 para credenciais inválidas")
    void deveRetornar401ParaCredenciaisInvalidas() {
        BadCredentialsException ex = new BadCredentialsException("Credenciais inválidas");

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleBadCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 400 para argumento inválido")
    void deveRetornar400ParaArgumentoInvalido() {
        IllegalArgumentException ex = new IllegalArgumentException("Valor inválido");

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 500 para exceção genérica")
    void deveRetornar500ParaExcecaoGenerica() {
        Exception ex = new RuntimeException("Erro inesperado");

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 400 para erro de validação")
    void deveRetornar400ParaErroValidacao() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "campo", "mensagem de erro");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Deve retornar 400 para erro de deserialização JSON")
    void deveRetornar400ParaErroDeserializacao() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = excecaoHandler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertTrue(response.getBody().get("mensagem").toString().contains("Erro ao processar"));
    }
}
