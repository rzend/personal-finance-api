package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.aplicacao.dto.CalculoCambioRequest;
import br.com.gestao.financeira.aplicacao.dto.ConversaoDto;
import br.com.gestao.financeira.aplicacao.dto.MoedaDto;
import br.com.gestao.financeira.aplicacao.dto.TaxaCambioDto;
import br.com.gestao.financeira.dominio.modelo.Moeda;
import br.com.gestao.financeira.dominio.services.CambioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de câmbio.
 */
@RestController
@RequestMapping("/cambio")
@Tag(name = "Câmbio", description = "Operações de câmbio e conversão de moedas")
public class CambioController {

    private final CambioService cambioServico;

    public CambioController(CambioService cambioServico) {
        this.cambioServico = cambioServico;
    }

    @GetMapping("/moedas")
    @Operation(summary = "Listar moedas", description = "Retorna lista de moedas suportadas pelo sistema")
    public ResponseEntity<List<MoedaDto>> listarMoedas() {
        List<Moeda> moedas = cambioServico.listarMoedasSuportadas();
        List<MoedaDto> dtos = moedas.stream()
                .map(m -> new MoedaDto(m.getCodigo(), m.getNome(), m.getTipoMoeda()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/taxa")
    @Operation(summary = "Obter taxa de câmbio", description = "Retorna a taxa de câmbio atual entre duas moedas")
    public ResponseEntity<TaxaCambioDto> obterTaxa(
            @RequestParam String origem,
            @RequestParam String destino) {
        TaxaCambioDto taxa = cambioServico.obterTaxaAtualDto(origem, destino);
        return ResponseEntity.ok(taxa);
    }

    @PostMapping("/calcular-custo")
    @Operation(summary = "Calcular custo de câmbio", description = "Calcula o custo total de uma operação de câmbio incluindo margem/spread")
    public ResponseEntity<ConversaoDto> calcularCusto(@Valid @RequestBody CalculoCambioRequest request) {
        ConversaoDto resultado = cambioServico.calcularCustoDoCambio(
                request.getValor(),
                request.getMoedaOrigem(),
                request.getMoedaDestino(),
                request.getMargem());
        return ResponseEntity.ok(resultado);
    }
}
