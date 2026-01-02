package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.dominio.entity.Familia;
import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.enums.PerfilUsuario;
import br.com.gestao.financeira.dominio.repository.FamiliaRepository;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FamiliaService {

    private final FamiliaRepository familiaRepository;
    private final UsuarioRepository usuarioRepository;

    public FamiliaService(FamiliaRepository familiaRepository, UsuarioRepository usuarioRepository) {
        this.familiaRepository = familiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Familia criarFamilia(String nome, Long usuarioId) {
        Familia familia = new Familia(nome);
        familia = familiaRepository.save(familia);

        Usuario gestor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Define o usuário como parte da família
        gestor.setFamilia(familia);
        gestor.setPerfil(PerfilUsuario.GESTOR);
        usuarioRepository.save(gestor);

        return familia;
    }

    @Transactional
    public void adicionarMembro(Long familiaId, Long usuarioId, String emailGestor) {
        Usuario gestor = usuarioRepository.findByEmail(emailGestor)
                .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

        // Validação de segurança: Gestor só mexe na própria família (exceto MASTER)
        boolean isMaster = gestor.getPerfil() == PerfilUsuario.MASTER;
        if (!isMaster) {
            if (gestor.getFamilia() == null || !gestor.getFamilia().getId().equals(familiaId)) {
                throw new RuntimeException("Acesso negado: Você não tem permissão para gerenciar esta família.");
            }
        }

        Familia familia = familiaRepository.findById(familiaId)
                .orElseThrow(() -> new RuntimeException("Família não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(
                        () -> new UsuarioService.UsuarioNaoEncontradoException(
                                usuarioId));

        usuario.setFamilia(familia);
        usuario.setPerfil(PerfilUsuario.USUARIO); // Garante que é MEMBER
                                                                                         // (padrão)
        usuarioRepository.save(usuario);
    }
}
