package br.com.gestao.financeira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Aplicação principal - Gestão Financeira API.
 */
@SpringBootApplication
@EnableCaching
public class GestaoFinanceiraApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestaoFinanceiraApplication.class, args);
    }
}




