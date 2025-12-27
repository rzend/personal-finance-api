package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.aplicacao.dto.AnaliseDespesasDto;
import br.com.gestao.financeira.dominio.services.AnaliseDespesasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller REST para análise de despesas.
 */
@RestController
@RequestMapping("/analise")
@Tag(name = "Análise", description = "Operações de análise de despesas")
public class AnaliseController {

    private final AnaliseDespesasService analiseDespesasServico;

    public AnaliseController(AnaliseDespesasService analiseDespesasServico) {
        this.analiseDespesasServico = analiseDespesasServico;
    }

    @GetMapping("/despesas")
    @Operation(summary = "Analisar despesas", description = "Retorna análise detalhada de despesas por categoria e período")
    public ResponseEntity<AnaliseDespesasDto> analisarDespesas(
            @RequestParam Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(defaultValue = "BRL") String moedaPadrao) {

        AnaliseDespesasDto analise = analiseDespesasServico.analisar(usuarioId, inicio, fim, moedaPadrao);
        return ResponseEntity.ok(analise);
    }
}





