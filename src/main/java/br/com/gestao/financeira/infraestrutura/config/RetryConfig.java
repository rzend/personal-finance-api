package br.com.gestao.financeira.infraestrutura.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Configuração para habilitar Spring Retry na aplicação.
 * Permite que métodos anotados com @Retryable tenham retry automático.
 */
@Configuration
@EnableRetry
public class RetryConfig {
}
