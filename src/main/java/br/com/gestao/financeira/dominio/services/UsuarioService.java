package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.dominio.enums.PerfilUsuario;
import br.com.gestao.financeira.dominio.enums.StatusUsuario;
import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de domínio responsável pela gestão de usuários.
 */
@Service
@Transactional
@SuppressWarnings("null")
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo usuário no sistema.
     * 
     * @param usuario dados do usuário a criar
     * @return o usuário criado
     * @throws IllegalArgumentException se email ou CPF já existem
     */
    public Usuario criarUsuario(Usuario usuario) {
        validarNovoUsuario(usuario);

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setPerfil(PerfilUsuario.USUARIO);
        usuario.setCriadoEm(LocalDateTime.now());

        if (usuario.getMoedaPadrao() == null || usuario.getMoedaPadrao().isBlank()) {
            usuario.setMoedaPadrao("BRL");
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Atualiza um usuário existente.
     * 
     * @param id          identificador do usuário
     * @param atualizacao dados para atualização
     * @return o usuário atualizado
     * @throws UsuarioNaoEncontradoException se usuário não existe
     */
    public Usuario atualizarUsuario(Long id, Usuario atualizacao) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        if (atualizacao.getNomeCompleto() != null) {
            existente.setNomeCompleto(atualizacao.getNomeCompleto());
        }
        if (atualizacao.getMoedaPadrao() != null) {
            existente.setMoedaPadrao(atualizacao.getMoedaPadrao());
        }
        if (atualizacao.getStatus() != null) {
            existente.setStatus(atualizacao.getStatus());
        }

        return usuarioRepository.save(existente);
    }

    /**
     * Exclui um usuário pelo ID.
     * 
     * @param id identificador do usuário
     * @throws UsuarioNaoEncontradoException se usuário não existe
     */
    public void excluirUsuario(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException(id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Obtém os detalhes de um usuário.
     * 
     * @param id identificador do usuário
     * @return o usuário encontrado
     * @throws UsuarioNaoEncontradoException se usuário não existe
     */
    public Usuario detalharUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    /**
     * Busca um usuário pelo email.
     * 
     * @param email email do usuário
     * @return o usuário encontrado
     * @throws UsuarioNaoEncontradoException se usuário não existe
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Email não encontrado: " + email));
    }

    /**
     * Lista todos os usuários.
     * 
     * @return lista de usuários
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorFamilia(Long familiaId) {
        return usuarioRepository.findByFamiliaId(familiaId);
    }

    private void validarNovoUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new EmailJaCadastradoException(usuario.getEmail());
        }
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new CpfJaCadastradoException(usuario.getCpf());
        }
    }

    // Exceções internas
    public static class UsuarioNaoEncontradoException extends RuntimeException {
        public UsuarioNaoEncontradoException(Long id) {
            super("Usuário não encontrado com ID: " + id);
        }

        public UsuarioNaoEncontradoException(String mensagem) {
            super(mensagem);
        }
    }

    public static class EmailJaCadastradoException extends RuntimeException {
        public EmailJaCadastradoException(String email) {
            super("Email já cadastrado: " + email);
        }
    }

    public static class CpfJaCadastradoException extends RuntimeException {
        public CpfJaCadastradoException(String cpf) {
            super("CPF já cadastrado: " + cpf);
        }
    }
}
