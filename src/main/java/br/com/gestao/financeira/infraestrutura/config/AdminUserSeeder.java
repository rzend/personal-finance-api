package br.com.gestao.financeira.infraestrutura.config;

import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.enums.PerfilUsuario;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Configuration
public class AdminUserSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserSeeder.class);

    @org.springframework.beans.factory.annotation.Value("${admin.default-password:admin123}")
    private String defaultPassword;

    @org.springframework.beans.factory.annotation.Value("${admin.email:admin@gestao.com}")
    private String adminEmail;

    private final UsuarioRepository usuarioRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AdminUserSeeder(UsuarioRepository usuarioRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(adminEmail);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getPerfil() != PerfilUsuario.MASTER) {
                usuario.setPerfil(PerfilUsuario.MASTER);
                usuarioRepository.save(usuario);
                logger.info("Admin user permissions updated to MASTER for {}.", adminEmail);
            }
        } else {
            logger.info("Creating default admin user for {}...", adminEmail);
            Usuario admin = new Usuario();
            admin.setNomeCompleto("Administrador");
            admin.setEmail(adminEmail);
            admin.setCpf("00000000000"); // CPF fica o mesmo pois é apenas seed
            admin.setSenha(passwordEncoder.encode(defaultPassword));
            admin.setPerfil(PerfilUsuario.MASTER);
            admin.setStatus(br.com.gestao.financeira.dominio.enums.StatusUsuario.ATIVO);

            usuarioRepository.save(admin);
            logger.info("Default admin user created successfully ({})", adminEmail);
        }
    }
}
