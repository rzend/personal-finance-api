package br.com.gestao.financeira.infraestrutura.config;

import br.com.gestao.financeira.dominio.services.CambioService;
import br.com.gestao.financeira.dominio.services.TransacaoService;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler global de exceções com mensagens em português.
 */
@RestControllerAdvice
public class ExcecaoHandler {

    private static final Logger log = LoggerFactory.getLogger(ExcecaoHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String mensagem = "Erro ao processar requisição JSON";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String valoresValidos = Arrays.stream(ife.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                mensagem = "Valor '" + ife.getValue() + "' inválido. Valores aceitos: " + valoresValidos;
            }
        }

        log.warn("Erro de deserialização: {}", mensagem);
        return ResponseEntity.badRequest()
                .body(criarRespostaErro(400, mensagem, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(campo, mensagem);
        });

        Map<String, Object> resposta = criarRespostaErro(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                erros);

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(UsuarioService.UsuarioNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNaoEncontrado(
            UsuarioService.UsuarioNaoEncontradoException ex) {
        log.warn("Usuário não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(criarRespostaErro(404, ex.getMessage(), null));
    }

    @ExceptionHandler(UsuarioService.EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailJaCadastrado(
            UsuarioService.EmailJaCadastradoException ex) {
        log.warn("Email já cadastrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(criarRespostaErro(409, ex.getMessage(), null));
    }

    @ExceptionHandler(UsuarioService.CpfJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleCpfJaCadastrado(
            UsuarioService.CpfJaCadastradoException ex) {
        log.warn("CPF já cadastrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(criarRespostaErro(409, ex.getMessage(), null));
    }

    @ExceptionHandler(TransacaoService.TransacaoNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleTransacaoNaoEncontrada(
            TransacaoService.TransacaoNaoEncontradaException ex) {
        log.warn("Transação não encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(criarRespostaErro(404, ex.getMessage(), null));
    }

    @ExceptionHandler(CambioService.MoedaNaoSuportadaException.class)
    public ResponseEntity<Map<String, Object>> handleMoedaNaoSuportada(
            CambioService.MoedaNaoSuportadaException ex) {
        log.warn("Moeda não suportada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(criarRespostaErro(400, ex.getMessage(), null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Credenciais inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(criarRespostaErro(401, "Credenciais inválidas", null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(criarRespostaErro(400, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Erro interno: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(criarRespostaErro(500, "Erro interno do servidor", null));
    }

    private Map<String, Object> criarRespostaErro(int status, String mensagem, Object detalhes) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", LocalDateTime.now().toString());
        resposta.put("status", status);
        resposta.put("mensagem", mensagem);
        if (detalhes != null) {
            resposta.put("detalhes", detalhes);
        }
        return resposta;
    }
}
