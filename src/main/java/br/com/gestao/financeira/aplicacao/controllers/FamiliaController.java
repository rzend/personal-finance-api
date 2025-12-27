package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.aplicacao.dto.FamiliaCriacaoDto;
import br.com.gestao.financeira.aplicacao.dto.MembroAdicaoDto;
import br.com.gestao.financeira.aplicacao.dto.UsuarioDto;

import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.services.FamiliaService;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/familias")
@Tag(name = "Famílias", description = "Operações de gestão de famílias e membros")
@SecurityRequirement(name = "bearerAuth")
public class FamiliaController {

    private final FamiliaService familiaService;
    private final UsuarioService usuarioService;

    public FamiliaController(FamiliaService familiaService, UsuarioService usuarioService) {
        this.familiaService = familiaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USUARIO', 'ROLE_GESTOR', 'ROLE_MASTER')")
    @Operation(summary = "Criar família", description = "Cria uma nova família e define o usuário atual como gestor")
    public ResponseEntity<Void> criarFamilia(@Valid @RequestBody FamiliaCriacaoDto dto, Principal principal) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(principal.getName());
        familiaService.criarFamilia(dto.getNome(), usuarioLogado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/membros")
    @PreAuthorize("hasAnyAuthority('ROLE_GESTOR', 'ROLE_MASTER')")
    @Operation(summary = "Adicionar membro", description = "Adiciona um usuário existente à família")
    public ResponseEntity<Void> adicionarMembro(@PathVariable Long id, @Valid @RequestBody MembroAdicaoDto dto,
            Principal principal) {
        familiaService.adicionarMembro(id, dto.getUsuarioId(), principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/meus-membros")
    @PreAuthorize("hasAnyAuthority('ROLE_USUARIO', 'ROLE_GESTOR', 'ROLE_MASTER')")
    @Operation(summary = "Listar membros da minha família", description = "Retorna os usuários da família do usuário atual")
    public ResponseEntity<List<UsuarioDto>> listarMeusMembros(Principal principal) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(principal.getName());

        if (usuarioLogado.getFamilia() == null) {
            return ResponseEntity.noContent().build();
        }

        List<UsuarioDto> usuarios = usuarioService.listarPorFamilia(usuarioLogado.getFamilia().getId()).stream()
                .map(this::paraDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    // Método auxiliar para conversão (pode ser movido para um mapper futuramente)
    private UsuarioDto paraDto(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getMoedaPadrao(),
                usuario.getStatus(),
                usuario.getCriadoEm(),
                usuario.getFamilia() != null ? usuario.getFamilia().getId() : null);
    }
}
