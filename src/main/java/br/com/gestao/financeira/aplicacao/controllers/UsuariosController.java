package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.aplicacao.dto.UsuarioCriacaoDto;
import br.com.gestao.financeira.aplicacao.dto.UsuarioDto;
import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import br.com.gestao.financeira.infraestrutura.components.ImportacaoExcelServico;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gestão de usuários.
 */
import org.springframework.security.access.prepost.PreAuthorize; // Add import

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Operações de gestão de usuários")
public class UsuariosController {

    private final UsuarioService usuariosServico;
    private final ImportacaoExcelServico importacaoServico;

    public UsuariosController(UsuarioService usuariosServico,
            ImportacaoExcelServico importacaoServico) {
        this.usuariosServico = usuariosServico;
        this.importacaoServico = importacaoServico;
    }

    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    public ResponseEntity<UsuarioDto> criar(@Valid @RequestBody UsuarioCriacaoDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setSenha(dto.getSenha());
        usuario.setMoedaPadrao(dto.getMoedaPadrao());

        Usuario criado = usuariosServico.criarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDto(criado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar usuário", description = "Retorna os detalhes de um usuário")
    public ResponseEntity<UsuarioDto> detalhar(@PathVariable Long id) {
        Usuario usuario = usuariosServico.detalharUsuario(id);
        return ResponseEntity.ok(paraDto(usuario));
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados")
    @PreAuthorize("hasAuthority('ROLE_MASTER')")
    public ResponseEntity<List<UsuarioDto>> listar() {
        List<UsuarioDto> usuarios = usuariosServico.listarTodos().stream()
                .map(this::paraDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário")
    @PreAuthorize("hasAuthority('ROLE_MASTER')")
    public ResponseEntity<UsuarioDto> atualizar(@PathVariable Long id,
            @RequestBody UsuarioCriacaoDto dto) {
        Usuario atualizacao = new Usuario();
        atualizacao.setNomeCompleto(dto.getNomeCompleto());
        atualizacao.setMoedaPadrao(dto.getMoedaPadrao());

        Usuario atualizado = usuariosServico.atualizarUsuario(id, atualizacao);
        return ResponseEntity.ok(paraDto(atualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema")
    @PreAuthorize("hasAuthority('ROLE_MASTER')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuariosServico.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/importar-excel")
    @Operation(summary = "Importar usuários via Excel", description = "Importa usuários em massa a partir de planilha Excel")
    @PreAuthorize("hasAuthority('ROLE_MASTER')")
    public ResponseEntity<br.com.gestao.financeira.aplicacao.dto.ResultadoImportacaoDto> importarExcel(
            @RequestParam("arquivo") MultipartFile arquivo) {
        br.com.gestao.financeira.aplicacao.dto.ResultadoImportacaoDto resultado = importacaoServico.importar(arquivo);
        return ResponseEntity.ok(resultado);
    }

    private UsuarioDto paraDto(Usuario usuario) {
        return new UsuarioDto(usuario.getId(), usuario.getNomeCompleto(), usuario.getEmail(), usuario.getCpf(),
                usuario.getMoedaPadrao(), usuario.getStatus(), usuario.getCriadoEm(),
                usuario.getFamilia() != null ? usuario.getFamilia().getId() : null);
    }
}
