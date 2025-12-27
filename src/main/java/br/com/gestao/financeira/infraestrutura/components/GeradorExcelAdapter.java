package br.com.gestao.financeira.infraestrutura.components;

import br.com.gestao.financeira.aplicacao.dto.ParametrosRelatorio;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.services.TransacaoService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Adaptador para geração de relatórios em Excel usando Apache POI.
 */
@Component
public class GeradorExcelAdapter {

    private static final Logger log = LoggerFactory.getLogger(GeradorExcelAdapter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TransacaoService transacaoService;

    public GeradorExcelAdapter(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /**
     * Gera um relatório de transações em formato Excel (XLSX).
     */
    public byte[] gerarRelatorio(ParametrosRelatorio params) {
        log.info("Gerando relatório Excel para usuário: {}", params.getUsuarioId());

        List<Transacao> transacoes = buscarTransacoes(params);

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Transações");

            // Estilos
            CellStyle headerStyle = criarEstiloCabecalho(workbook);
            CellStyle dataStyle = criarEstiloDados(workbook);
            CellStyle moneyStyle = criarEstiloMonetario(workbook);

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Data", "Tipo", "Categoria", "Moeda", "Valor", "Descrição" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dados
            int rowNum = 1;
            for (Transacao t : transacoes) {
                Row row = sheet.createRow(rowNum++);

                Cell cellId = row.createCell(0);
                cellId.setCellValue(t.getId());
                cellId.setCellStyle(dataStyle);

                Cell cellData = row.createCell(1);
                cellData.setCellValue(t.getData().format(DATE_FORMATTER));
                cellData.setCellStyle(dataStyle);

                Cell cellTipo = row.createCell(2);
                cellTipo.setCellValue(t.getTipo().getDescricao());
                cellTipo.setCellStyle(dataStyle);

                Cell cellCategoria = row.createCell(3);
                cellCategoria.setCellValue(t.getCategoria().getDescricao());
                cellCategoria.setCellStyle(dataStyle);

                Cell cellMoeda = row.createCell(4);
                cellMoeda.setCellValue(t.getMoedaOriginal());
                cellMoeda.setCellStyle(dataStyle);

                Cell cellValor = row.createCell(5);
                cellValor.setCellValue(t.getValorOriginal().doubleValue());
                cellValor.setCellStyle(moneyStyle);

                Cell cellDescricao = row.createCell(6);
                cellDescricao.setCellValue(t.getDescricao() != null ? t.getDescricao() : "");
                cellDescricao.setCellStyle(dataStyle);
            }

            // Auto-ajusta largura das colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            log.info("Relatório Excel gerado com sucesso. {} transações.", transacoes.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erro ao gerar relatório Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar relatório Excel: " + e.getMessage(), e);
        }
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle criarEstiloDados(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle criarEstiloMonetario(Workbook workbook) {
        CellStyle style = criarEstiloDados(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private List<Transacao> buscarTransacoes(ParametrosRelatorio params) {
        return transacaoService.listarTransacoes(
                params.getUsuarioId(),
                params.getDataInicio(),
                params.getDataFim(),
                null,
                params.getMoeda(),
                Pageable.unpaged()).getContent();
    }
}
