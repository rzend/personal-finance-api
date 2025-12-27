package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.dominio.repository.SaldoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controller REST para consulta de saldo de conta.
 */
@RestController
@RequestMapping("/saldo-conta")
@Tag(name = "Saldo de Conta", description = "Operações de consulta de saldo bancário")
public class SaldoContaController {

    private final SaldoRepository saldoContaPort;

    public SaldoContaController(SaldoRepository saldoContaPort) {
        this.saldoContaPort = saldoContaPort;
    }

    @GetMapping
    @Operation(summary = "Obter saldo", description = "Retorna o saldo atual da conta do usuário")
    public ResponseEntity<Map<String, Object>> obterSaldo(@RequestParam Long usuarioId) {
        BigDecimal saldo = saldoContaPort.obterSaldoAtual(usuarioId);

        return ResponseEntity.ok(Map.of(
                "usuarioId", usuarioId,
                "saldo", saldo,
                "moeda", "BRL"));
    }
}





