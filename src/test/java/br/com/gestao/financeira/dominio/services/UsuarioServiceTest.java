package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.dominio.entity.Usuario;

import br.com.gestao.financeira.dominio.enums.PerfilUsuario;
import br.com.gestao.financeira.dominio.enums.StatusUsuario;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
@SuppressWarnings("null")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Teste Usuario");
        usuario.setEmail("teste@email.com");
        usuario.setCpf("12345678901");
        usuario.setSenha("senha123");
        usuario.setMoedaPadrao("BRL");
        usuario.setStatus(StatusUsuario.ATIVO);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.criarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("Teste Usuario", resultado.getNomeCompleto());
        assertEquals(PerfilUsuario.USUARIO, resultado.getPerfil());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UsuarioService.EmailJaCadastradoException.class,
                () -> usuarioService.criarUsuario(usuario));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void deveLancarExcecaoQuandoCpfJaExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(UsuarioService.CpfJaCadastradoException.class,
                () -> usuarioService.criarUsuario(usuario));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario atualizacao = new Usuario();
        atualizacao.setNomeCompleto("Nome Atualizado");
        atualizacao.setMoedaPadrao("USD");

        Usuario resultado = usuarioService.atualizarUsuario(1L, atualizacao);

        assertNotNull(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioService.UsuarioNaoEncontradoException.class,
                () -> usuarioService.atualizarUsuario(999L, new Usuario()));
    }

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void deveExcluirUsuarioComSucesso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        assertDoesNotThrow(() -> usuarioService.excluirUsuario(1L));
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário inexistente")
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertThrows(UsuarioService.UsuarioNaoEncontradoException.class,
                () -> usuarioService.excluirUsuario(999L));
    }

    @Test
    @DisplayName("Deve detalhar usuário com sucesso")
    void deveDetalharUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.detalharUsuario(1L);

        assertNotNull(resultado);
        assertEquals("Teste Usuario", resultado.getNomeCompleto());
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void deveBuscarUsuarioPorEmailComSucesso() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorEmail("teste@email.com");

        assertNotNull(resultado);
        assertEquals("teste@email.com", resultado.getEmail());
    }
}
