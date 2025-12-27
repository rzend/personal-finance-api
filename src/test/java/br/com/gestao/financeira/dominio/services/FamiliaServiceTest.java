package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.dominio.entity.Familia;
import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.enums.PerfilUsuario;
import br.com.gestao.financeira.dominio.repository.FamiliaRepository;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FamiliaService Tests")
class FamiliaServiceTest {

    @Mock
    private FamiliaRepository familiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private FamiliaService familiaService;

    private Usuario gestor;
    private Usuario membro;
    private Familia familia;

    @BeforeEach
    void setUp() {
        gestor = new Usuario();
        gestor.setId(1L);
        gestor.setNomeCompleto("Gestor Pai");
        gestor.setPerfil(PerfilUsuario.USUARIO); // Inicialmente usuário comum

        membro = new Usuario();
        membro.setId(2L);
        membro.setNomeCompleto("Filho Membro");
        membro.setPerfil(PerfilUsuario.USUARIO);

        familia = new Familia("Família Silva");
        familia.setId(100L);
    }

    @Test
    @DisplayName("Deve criar família com sucesso e atribuir gestor")
    void deveCriarFamiliaComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(gestor));
        when(familiaRepository.save(any(Familia.class))).thenReturn(familia);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(gestor);

        Familia resultado = familiaService.criarFamilia("Família Silva", gestor.getId());

        assertNotNull(resultado);
        assertEquals("Família Silva", resultado.getNome());

        verify(usuarioRepository).save(gestor);
        verify(familiaRepository).save(any(Familia.class));
    }

    @Test
    @DisplayName("Deve adicionar membro com sucesso")
    void deveAdicionarMembroComSucesso() {
        gestor.setFamilia(familia); // Gestor precisa ser da família
        when(usuarioRepository.findByEmail("gestor@email.com")).thenReturn(Optional.of(gestor));
        when(familiaRepository.findById(100L)).thenReturn(Optional.of(familia));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(membro));

        familiaService.adicionarMembro(100L, 2L, "gestor@email.com");

        assertEquals(familia, membro.getFamilia());
        assertEquals(PerfilUsuario.USUARIO, membro.getPerfil());
        verify(usuarioRepository).save(membro);
    }

    @Test
    @DisplayName("Deve lançar exceção se gestor não tem permissão")
    void deveLancarExcecaoAcessoNegado() {
        gestor.setFamilia(new Familia("Outra Família"));
        gestor.getFamilia().setId(200L); // ID diferente

        when(usuarioRepository.findByEmail("gestor@email.com")).thenReturn(Optional.of(gestor));

        assertThrows(RuntimeException.class, () -> familiaService.adicionarMembro(100L, 2L, "gestor@email.com"));
    }

    @Test
    @DisplayName("Deve lançar exceção se família não existe")
    void deveLancarExcecaoFamiliaInexistente() {
        gestor.setFamilia(familia);
        gestor.getFamilia().setId(999L); // Match the target ID to pass security check

        when(usuarioRepository.findByEmail("gestor@email.com")).thenReturn(Optional.of(gestor));
        when(familiaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> familiaService.adicionarMembro(999L, 2L, "gestor@email.com"));
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existe")
    void deveLancarExcecaoUsuarioInexistente() {
        gestor.setFamilia(familia);
        when(usuarioRepository.findByEmail("gestor@email.com")).thenReturn(Optional.of(gestor));
        when(familiaRepository.findById(100L)).thenReturn(Optional.of(familia));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioService.UsuarioNaoEncontradoException.class,
                () -> familiaService.adicionarMembro(100L, 999L, "gestor@email.com"));
    }
}
