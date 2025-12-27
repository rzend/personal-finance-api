package br.com.gestao.financeira.infraestrutura.components;

import br.com.gestao.financeira.aplicacao.dto.ParametrosRelatorio;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.repository.RelatorioRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Adaptador para geração de relatórios em PDF usando iTextPDF.
 */
@Component
public class GeradorPdfAdapter implements RelatorioRepository {

    private static final Logger log = LoggerFactory.getLogger(GeradorPdfAdapter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final br.com.gestao.financeira.dominio.services.TransacaoService transacaoService;
    private final GeradorExcelAdapter geradorExcel;

    public GeradorPdfAdapter(br.com.gestao.financeira.dominio.services.TransacaoService transacaoService,
            GeradorExcelAdapter geradorExcel) {
        this.transacaoService = transacaoService;
        this.geradorExcel = geradorExcel;
    }

    @Override
    public byte[] gerarRelatorioPDF(ParametrosRelatorio params) {
        log.info("Gerando relatório PDF para usuário: {}", params.getUsuarioId());

        List<Transacao> transacoes = buscarTransacoes(params);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Relatório de Transações", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // Informações do relatório
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 10);
            String periodo = formatarPeriodo(params);
            document.add(new Paragraph("Período: " + periodo, infoFont));
            document.add(new Paragraph("Total de transações: " + transacoes.size(), infoFont));
            document.add(new Paragraph(" "));

            // Tabela de transações
            if (!transacoes.isEmpty()) {
                PdfPTable table = criarTabelaTransacoes(transacoes);
                document.add(table);
            } else {
                document.add(new Paragraph("Nenhuma transação encontrada no período.", infoFont));
            }

            document.close();
            log.info("Relatório PDF gerado com sucesso. {} transações.", transacoes.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erro ao gerar relatório PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar relatório PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] gerarRelatorioExcel(ParametrosRelatorio params) {
        // Delega para o adaptador Excel
        return geradorExcel.gerarRelatorio(params);
    }

    private PdfPTable criarTabelaTransacoes(List<Transacao> transacoes) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1.5f, 2f, 2f, 1.5f, 2f, 2.5f });

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        BaseColor headerColor = new BaseColor(66, 139, 202);

        // Cabeçalhos
        String[] headers = { "Data", "Tipo", "Categoria", "Moeda", "Valor", "Descrição" };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Dados
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 9);
        for (Transacao t : transacoes) {
            table.addCell(new Phrase(t.getData().format(DATE_FORMATTER), dataFont));
            table.addCell(new Phrase(t.getTipo().getDescricao(), dataFont));
            table.addCell(new Phrase(t.getCategoria().getDescricao(), dataFont));
            table.addCell(new Phrase(t.getMoedaOriginal(), dataFont));
            table.addCell(new Phrase(t.getValorOriginal().toString(), dataFont));
            table.addCell(new Phrase(t.getDescricao() != null ? t.getDescricao() : "", dataFont));
        }

        return table;
    }

    private List<Transacao> buscarTransacoes(ParametrosRelatorio params) {
        return transacaoService.listarTransacoes(
                params.getUsuarioId(),
                params.getDataInicio(),
                params.getDataFim(),
                null, // categoria
                params.getMoeda(),
                Pageable.unpaged()).getContent();
    }

    private String formatarPeriodo(ParametrosRelatorio params) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String inicio = params.getDataInicio() != null
                ? params.getDataInicio().format(formatter)
                : "início";
        String fim = params.getDataFim() != null
                ? params.getDataFim().format(formatter)
                : "atual";
        return inicio + " a " + fim;
    }
}
