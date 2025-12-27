package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.aplicacao.dto.TransacaoCriacaoDto;
import br.com.gestao.financeira.aplicacao.dto.TransacaoDto;
import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.services.CambioService;
import br.com.gestao.financeira.dominio.services.TransacaoService;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller REST para gestão de transações.
 */
@RestController
@RequestMapping("/transacoes")
@Tag(name = "Transações", description = "Operações de gestão de transações financeiras")
@SuppressWarnings("null")
public class TransacoesController {

    private final TransacaoService transacoesServico;
    private final CambioService cambioServico;
    private final UsuarioRepository usuarioRepositorio;

    private final br.com.gestao.financeira.dominio.services.UsuarioService usuarioService;

    public TransacoesController(TransacaoService transacoesServico,
            CambioService cambioServico,
            UsuarioRepository usuarioRepositorio,
            br.com.gestao.financeira.dominio.services.UsuarioService usuarioService) {
        this.transacoesServico = transacoesServico;
        this.cambioServico = cambioServico;
        this.usuarioRepositorio = usuarioRepositorio;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Registrar transação", description = "Registra uma nova transação financeira")
    public ResponseEntity<TransacaoDto> registrar(@Valid @RequestBody TransacaoCriacaoDto dto) {
        Transacao transacao = new Transacao();
        transacao.setUsuarioId(dto.getUsuarioId());
        transacao.setTipo(dto.getTipo());
        transacao.setValorOriginal(dto.getValor());
        transacao.setMoedaOriginal(dto.getMoeda());
        transacao.setCategoria(dto.getCategoria());
        transacao.setDescricao(dto.getDescricao());

        Transacao registrada = transacoesServico.registrarTransacao(transacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDto(registrada));
    }

    @GetMapping
    @Operation(summary = "Listar transações", description = "Lista transações com filtros opcionais e paginação")
    public ResponseEntity<Page<TransacaoDto>> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) CategoriaTransacao categoria,
            @RequestParam(required = false) String moeda,
            Pageable pageable,
            java.security.Principal principal) {

        if (usuarioId != null) {
            validarAcesso(usuarioId, principal);
        }

        Page<Transacao> transacoes = transacoesServico.listarTransacoes(usuarioId, inicio, fim, categoria, moeda,
                pageable);
        Page<TransacaoDto> dtos = transacoes.map(this::paraDto);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar transação", description = "Retorna os detalhes de uma transação")
    public ResponseEntity<TransacaoDto> detalhar(@PathVariable Long id) {
        Transacao transacao = transacoesServico.detalharTransacao(id);
        return ResponseEntity.ok(paraDto(transacao));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação", description = "Atualiza os dados de uma transação")
    public ResponseEntity<TransacaoDto> atualizar(@PathVariable Long id,
            @RequestBody TransacaoCriacaoDto dto) {
        Transacao atualizacao = new Transacao();
        atualizacao.setValorOriginal(dto.getValor());
        atualizacao.setMoedaOriginal(dto.getMoeda());
        atualizacao.setCategoria(dto.getCategoria());
        atualizacao.setDescricao(dto.getDescricao());
        atualizacao.setTipo(dto.getTipo());

        Transacao atualizada = transacoesServico.atualizarTransacao(id, atualizacao);
        return ResponseEntity.ok(paraDto(atualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transação", description = "Remove uma transação")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        transacoesServico.excluirTransacao(id);
        return ResponseEntity.noContent().build();
    }

    private void validarAcesso(Long usuarioAlvoId, java.security.Principal principal) {
        br.com.gestao.financeira.dominio.entity.Usuario solicitante = usuarioService
                .buscarPorEmail(principal.getName());
        br.com.gestao.financeira.dominio.entity.Usuario alvo = usuarioService.detalharUsuario(usuarioAlvoId);

        // 1. O próprio usuário
        if (solicitante.getId().equals(alvo.getId())) {
            return;
        }

        // 2. Administrador (MASTER)
        if (solicitante.getPerfil() == br.com.gestao.financeira.dominio.enums.PerfilUsuario.MASTER) {
            return;
        }

        // 3. Gestor da familia do usuario alvo
        if (solicitante.getPerfil() == br.com.gestao.financeira.dominio.enums.PerfilUsuario.GESTOR &&
                solicitante.getFamilia() != null &&
                alvo.getFamilia() != null &&
                solicitante.getFamilia().getId().equals(alvo.getFamilia().getId())) {
            return;
        }

        throw new RuntimeException(
                "Acesso negado: Você não tem permissão para visualizar as transações deste usuário.");
    }

    private TransacaoDto paraDto(Transacao t) {
        TransacaoDto dto = new TransacaoDto();
        dto.setId(t.getId());
        dto.setUsuarioId(t.getUsuarioId());
        dto.setTipo(t.getTipo());
        dto.setValorOriginal(t.getValorOriginal());
        dto.setMoedaOriginal(t.getMoedaOriginal());
        dto.setCategoria(t.getCategoria());
        dto.setData(t.getData());
        dto.setDescricao(t.getDescricao());
        dto.setTaxaCambioAplicada(t.getTaxaCambioAplicada());

        // Calcula valor convertido para moeda padrão do usuário
        try {
            usuarioRepositorio.findById(t.getUsuarioId()).ifPresent(usuario -> {
                if (!t.getMoedaOriginal().equals(usuario.getMoedaPadrao())) {
                    var conversao = cambioServico.calcularCustoDoCambio(
                            t.getValorOriginal(),
                            t.getMoedaOriginal(),
                            usuario.getMoedaPadrao(),
                            java.math.BigDecimal.ZERO);
                    dto.setValorConvertido(conversao.getValorConvertido());
                    dto.setMoedaConvertida(usuario.getMoedaPadrao());
                    dto.setTaxaCambioAplicada(conversao.getTaxa());
                }
            });
        } catch (Exception e) {
            // Ignora erro de conversão, mantém valores originais
        }

        return dto;
    }
}
