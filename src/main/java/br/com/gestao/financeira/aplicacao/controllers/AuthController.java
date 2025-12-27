package br.com.gestao.financeira.aplicacao.controllers;

import br.com.gestao.financeira.aplicacao.dto.LoginRequest;
import br.com.gestao.financeira.aplicacao.dto.LoginResponse;
import br.com.gestao.financeira.aplicacao.dto.UsuarioCriacaoDto;
import br.com.gestao.financeira.aplicacao.dto.UsuarioDto;
import br.com.gestao.financeira.infraestrutura.components.JwtUtils;
import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para autenticação.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Operações de login e registro")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuariosServico;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
            UsuarioService usuariosServico,
            JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usuariosServico = usuariosServico;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica um usuário e retorna token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.gerarToken(authentication);

        Usuario usuario = usuariosServico.buscarPorEmail(request.getEmail());

        return ResponseEntity.ok(new LoginResponse(
                token,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNomeCompleto()));
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar", description = "Registra um novo usuário no sistema")
    public ResponseEntity<UsuarioDto> registrar(@Valid @RequestBody UsuarioCriacaoDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setSenha(dto.getSenha());
        usuario.setMoedaPadrao(dto.getMoedaPadrao());

        Usuario criado = usuariosServico.criarUsuario(usuario);

        UsuarioDto resposta = new UsuarioDto(
                criado.getId(),
                criado.getNomeCompleto(),
                criado.getEmail(),
                criado.getCpf(),
                criado.getMoedaPadrao(),
                criado.getStatus(),
                criado.getCriadoEm(),
                null);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
