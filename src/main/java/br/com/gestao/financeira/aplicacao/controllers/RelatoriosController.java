package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.aplicacao.dto.ParametrosRelatorio;
import br.com.gestao.financeira.dominio.enums.PerfilUsuario;
import br.com.gestao.financeira.dominio.repository.RelatorioRepository;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

/**
 * Controller REST para download de relatórios.
 */
@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios", description = "Download de relatórios de transações")
@SuppressWarnings("null")
public class RelatoriosController {

        private final RelatorioRepository relatorioPort;
        private final UsuarioService usuarioService;

        public RelatoriosController(RelatorioRepository relatorioPort,
                        UsuarioService usuarioService) {
                this.relatorioPort = relatorioPort;
                this.usuarioService = usuarioService;
        }

        @GetMapping("/transacoes.pdf")
        @Operation(summary = "Download PDF", description = "Gera e baixa relatório de transações em PDF")
        public ResponseEntity<byte[]> downloadPdf(
                        @RequestParam Long usuarioId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                        @RequestParam(required = false) String moeda,
                        java.security.Principal principal) {

                validarAcesso(usuarioId, principal);

                ParametrosRelatorio params = new ParametrosRelatorio(
                                usuarioId,
                                inicio != null ? inicio.atStartOfDay() : null,
                                fim != null ? fim.atTime(23, 59, 59) : null,
                                moeda,
                                null);
                byte[] pdf = relatorioPort.gerarRelatorioPDF(params);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transacoes.pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdf);
        }

        @GetMapping("/transacoes.xlsx")
        @Operation(summary = "Download Excel", description = "Gera e baixa relatório de transações em Excel")
        public ResponseEntity<byte[]> downloadExcel(
                        @RequestParam Long usuarioId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                        @RequestParam(required = false) String moeda,
                        java.security.Principal principal) {

                validarAcesso(usuarioId, principal);

                ParametrosRelatorio params = new ParametrosRelatorio(
                                usuarioId,
                                inicio != null ? inicio.atStartOfDay() : null,
                                fim != null ? fim.atTime(23, 59, 59) : null,
                                moeda,
                                null);
                byte[] excel = relatorioPort.gerarRelatorioExcel(params);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transacoes.xlsx")
                                .contentType(
                                                MediaType.parseMediaType(
                                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .body(excel);
        }

        private void validarAcesso(Long usuarioAlvoId, Principal principal) {
                br.com.gestao.financeira.dominio.entity.Usuario solicitante = usuarioService
                                .buscarPorEmail(principal.getName());
                br.com.gestao.financeira.dominio.entity.Usuario alvo = usuarioService.detalharUsuario(usuarioAlvoId);

                // 1. O próprio usuário
                if (solicitante.getId().equals(alvo.getId())) {
                        return;
                }

                // 2. Administrador (MASTER)
                if (solicitante.getPerfil() == PerfilUsuario.MASTER) {
                        return;
                }

                // 3. Gestor da familia do usuario alvo
                if (solicitante.getPerfil() == PerfilUsuario.GESTOR &&
                                solicitante.getFamilia() != null &&
                                alvo.getFamilia() != null &&
                                solicitante.getFamilia().getId().equals(alvo.getFamilia().getId())) {
                        return;
                }

                throw new RuntimeException(
                                "Acesso negado: Você não tem permissão para visualizar o relatório deste usuário.");
        }
}
