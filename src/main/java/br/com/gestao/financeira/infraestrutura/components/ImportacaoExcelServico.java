package br.com.gestao.financeira.infraestrutura.components;

import br.com.gestao.financeira.aplicacao.dto.ResultadoImportacaoDto;
import br.com.gestao.financeira.dominio.entity.Usuario;
import br.com.gestao.financeira.dominio.services.UsuarioService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para importação de usuários via arquivo Excel.
 */
@Service
public class ImportacaoExcelServico {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoExcelServico.class);

    private final UsuarioService usuariosServico;

    public ImportacaoExcelServico(UsuarioService usuariosServico) {
        this.usuariosServico = usuariosServico;
    }

    /**
     * Importa usuários de um arquivo Excel.
     * Formato esperado: Nome Completo | Email | CPF | Senha | Moeda Padrão
     */
    public ResultadoImportacaoDto importar(MultipartFile arquivo) {
        log.info("Iniciando importação de usuários do arquivo: {}", arquivo.getOriginalFilename());

        List<String> erros = new ArrayList<>();
        int sucessos = 0;
        int falhas = 0;

        try (Workbook workbook = new XSSFWorkbook(arquivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalLinhas = sheet.getLastRowNum();

            // Pula o cabeçalho (linha 0)
            for (int i = 1; i <= totalLinhas; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    Usuario usuario = extrairUsuario(row, i);
                    usuariosServico.criarUsuario(usuario);
                    sucessos++;
                    log.debug("Usuário importado com sucesso: {}", usuario.getEmail());
                } catch (Exception e) {
                    falhas++;
                    String erro = "Linha " + (i + 1) + ": " + e.getMessage();
                    erros.add(erro);
                    log.warn("Erro ao importar linha {}: {}", i + 1, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Erro ao processar arquivo Excel: {}", e.getMessage(), e);
            erros.add("Erro ao processar arquivo: " + e.getMessage());
        }

        log.info("Importação concluída. Sucessos: {}, Falhas: {}", sucessos, falhas);
        return new ResultadoImportacaoDto(sucessos + falhas, sucessos, falhas, erros);
    }

    private Usuario extrairUsuario(Row row, int linha) {
        Usuario usuario = new Usuario();

        String nomeCompleto = getCellString(row.getCell(0));
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new IllegalArgumentException("Nome completo é obrigatório");
        }
        usuario.setNomeCompleto(nomeCompleto);

        String email = getCellString(row.getCell(1));
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        usuario.setEmail(email);

        String cpf = getCellString(row.getCell(2));
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        // Remove formatação do CPF
        cpf = cpf.replaceAll("[^0-9]", "");
        usuario.setCpf(cpf);

        String senha = getCellString(row.getCell(3));
        if (senha == null || senha.isBlank()) {
            senha = "senha123"; // Senha padrão se não informada
        }
        usuario.setSenha(senha);

        String moedaPadrao = getCellString(row.getCell(4));
        if (moedaPadrao == null || moedaPadrao.isBlank()) {
            moedaPadrao = "BRL";
        }
        usuario.setMoedaPadrao(moedaPadrao.toUpperCase());

        return usuario;
    }

    private String getCellString(Cell cell) {
        if (cell == null)
            return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }
}
