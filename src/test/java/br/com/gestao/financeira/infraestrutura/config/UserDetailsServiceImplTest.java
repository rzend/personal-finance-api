package br.com.gestao.financeira.infraestrutura.config;

import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.enums.PerfilUsuario;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Deve carregar usuário com perfil USUARIO")
    void deveCarregarUsuarioComPerfilUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("user@test.com");
        usuario.setSenha("encodedPass");
        usuario.setPerfil(PerfilUsuario.USUARIO);

        when(usuarioRepository.findByEmail("user@test.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@test.com");

        assertNotNull(userDetails);
        assertEquals("user@test.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO")));
    }

    @Test
    @DisplayName("Deve carregar usuário com perfil MASTER")
    void deveCarregarUsuarioComPerfilMaster() {
        Usuario usuario = new Usuario();
        usuario.setEmail("master@test.com");
        usuario.setSenha("encodedPass");
        usuario.setPerfil(PerfilUsuario.MASTER);

        when(usuarioRepository.findByEmail("master@test.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("master@test.com");

        assertNotNull(userDetails);
        assertEquals("master@test.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MASTER")));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown@test.com");
        });
    }
}
