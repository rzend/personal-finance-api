package br.com.gestao.financeira.infraestrutura.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Aspecto para auditoria de operações sensíveis.
 * Registra logs estruturados para operações CRUD em usuários e transações,
 * autenticação, e operações de câmbio.
 */
@Aspect
@Component
public class AuditoriaAspect {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDITORIA");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ==================== POINTCUTS ====================

    @Pointcut("execution(* br.com.gestao.financeira.dominio.services.UsuarioService.criar*(..))")
    public void criacaoUsuario() {
    }

    @Pointcut("execution(* br.com.gestao.financeira.dominio.services.UsuarioService.atualizar*(..))")
    public void atualizacaoUsuario() {
    }

    @Pointcut("execution(* br.com.gestao.financeira.dominio.services.UsuarioService.excluir*(..))")
    public void exclusaoUsuario() {
    }

    @Pointcut("execution(* br.com.gestao.financeira.dominio.services.TransacaoService.registrar*(..))")
    public void registroTransacao() {
    }

    @Pointcut("execution(* br.com.gestao.financeira.dominio.services.TransacaoService.atualizar*(..))")
    public void atualizacaoTransacao() {
    }

    @Pointcut("execution(* br.com.gestao.financeira.dominio.services.TransacaoService.excluir*(..))")
    public void exclusaoTransacao() {
    }

    @Pointcut("execution(* br.com.gestao.financeira.aplicacao.controllers.AuthController.login(..))")
    public void loginUsuario() {
    }

    @Pointcut("execution(* br.com.gestao.financeira.dominio.services.CambioService.calcularCustoDoCambio(..))")
    public void operacaoCambio() {
    }

    // ==================== ADVICES PARA USUÁRIOS ====================

    @AfterReturning(pointcut = "criacaoUsuario()", returning = "resultado")
    public void auditarCriacaoUsuario(JoinPoint joinPoint, Object resultado) {
        registrarAuditoria("USUARIO_CRIADO", joinPoint, resultado, null);
    }

    @AfterReturning(pointcut = "atualizacaoUsuario()", returning = "resultado")
    public void auditarAtualizacaoUsuario(JoinPoint joinPoint, Object resultado) {
        registrarAuditoria("USUARIO_ATUALIZADO", joinPoint, resultado, null);
    }

    @AfterReturning(pointcut = "exclusaoUsuario()")
    public void auditarExclusaoUsuario(JoinPoint joinPoint) {
        registrarAuditoria("USUARIO_EXCLUIDO", joinPoint, null, null);
    }

    @AfterThrowing(pointcut = "criacaoUsuario() || atualizacaoUsuario() || exclusaoUsuario()", throwing = "erro")
    public void auditarErroUsuario(JoinPoint joinPoint, Throwable erro) {
        registrarAuditoria("USUARIO_ERRO", joinPoint, null, erro);
    }

    // ==================== ADVICES PARA TRANSAÇÕES ====================

    @AfterReturning(pointcut = "registroTransacao()", returning = "resultado")
    public void auditarRegistroTransacao(JoinPoint joinPoint, Object resultado) {
        registrarAuditoria("TRANSACAO_REGISTRADA", joinPoint, resultado, null);
    }

    @AfterReturning(pointcut = "atualizacaoTransacao()", returning = "resultado")
    public void auditarAtualizacaoTransacao(JoinPoint joinPoint, Object resultado) {
        registrarAuditoria("TRANSACAO_ATUALIZADA", joinPoint, resultado, null);
    }

    @AfterReturning(pointcut = "exclusaoTransacao()")
    public void auditarExclusaoTransacao(JoinPoint joinPoint) {
        registrarAuditoria("TRANSACAO_EXCLUIDA", joinPoint, null, null);
    }

    @AfterThrowing(pointcut = "registroTransacao() || atualizacaoTransacao() || exclusaoTransacao()", throwing = "erro")
    public void auditarErroTransacao(JoinPoint joinPoint, Throwable erro) {
        registrarAuditoria("TRANSACAO_ERRO", joinPoint, null, erro);
    }

    // ==================== ADVICES PARA AUTENTICAÇÃO ====================

    @Around("loginUsuario()")
    public Object auditarLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String email = "desconhecido";

        if (args.length > 0 && args[0] != null) {
            try {
                // Tenta extrair email do LoginRequest
                Object loginRequest = args[0];
                java.lang.reflect.Method getEmail = loginRequest.getClass().getMethod("getEmail");
                email = (String) getEmail.invoke(loginRequest);
            } catch (Exception e) {
                // Ignora se não conseguir extrair
            }
        }

        try {
            Object resultado = joinPoint.proceed();
            auditLog.info("[AUDITORIA] Ação=LOGIN_SUCESSO | Usuario={} | Timestamp={} | IP={}",
                    email, LocalDateTime.now().format(FORMATTER), "N/A");
            return resultado;
        } catch (Throwable e) {
            auditLog.warn("[AUDITORIA] Ação=LOGIN_FALHA | Usuario={} | Timestamp={} | Motivo={}",
                    email, LocalDateTime.now().format(FORMATTER), e.getMessage());
            throw e;
        }
    }

    // ==================== ADVICES PARA CÂMBIO ====================

    @AfterReturning(pointcut = "operacaoCambio()", returning = "resultado")
    public void auditarOperacaoCambio(JoinPoint joinPoint, Object resultado) {
        Object[] args = joinPoint.getArgs();
        String detalhes = "";
        if (args.length >= 3) {
            detalhes = String.format("valor=%s moedaOrigem=%s moedaDestino=%s",
                    args[0], args[1], args[2]);
        }

        String usuario = obterUsuarioAtual();
        auditLog.info("[AUDITORIA] Ação=CAMBIO_CALCULADO | Usuario={} | Timestamp={} | Detalhes={}",
                usuario, LocalDateTime.now().format(FORMATTER), detalhes);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void registrarAuditoria(String acao, JoinPoint joinPoint, Object resultado, Throwable erro) {
        String usuario = obterUsuarioAtual();
        String metodo = joinPoint.getSignature().getName();
        String argumentos = formatarArgumentos(joinPoint.getArgs());
        String timestamp = LocalDateTime.now().format(FORMATTER);

        if (erro != null) {
            auditLog.warn("[AUDITORIA] Ação={} | Usuario={} | Método={} | Args={} | Timestamp={} | Erro={}",
                    acao, usuario, metodo, argumentos, timestamp, erro.getMessage());
        } else {
            String resultadoStr = resultado != null ? extrairId(resultado) : "N/A";
            auditLog.info("[AUDITORIA] Ação={} | Usuario={} | Método={} | Args={} | Timestamp={} | Resultado={}",
                    acao, usuario, metodo, argumentos, timestamp, resultadoStr);
        }
    }

    private String obterUsuarioAtual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception e) {
            // Ignora erros ao obter usuário
        }
        return "anonimo";
    }

    private String formatarArgumentos(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null)
                        return "null";
                    // Evita logar senhas
                    String str = arg.toString();
                    if (str.toLowerCase().contains("senha")) {
                        return "[REDACTED]";
                    }
                    // Limita tamanho
                    return str.length() > 100 ? str.substring(0, 100) + "..." : str;
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("[]");
    }

    private String extrairId(Object resultado) {
        try {
            java.lang.reflect.Method getId = resultado.getClass().getMethod("getId");
            Object id = getId.invoke(resultado);
            return id != null ? "id=" + id : resultado.getClass().getSimpleName();
        } catch (Exception e) {
            return resultado.getClass().getSimpleName();
        }
    }
}
