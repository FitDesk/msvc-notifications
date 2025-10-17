package com.msvcnotifications.services.implemts;

import com.msvcnotifications.services.JasperReportService;
import com.msvcnotifications.events.PaymentApprovedEvent;
import com.msvcnotifications.services.JasperReportService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class JasperReportServiceImpl implements JasperReportService {

    @Override
    public byte[] generatePaymentReceiptPdf(PaymentApprovedEvent paymentEvent) throws Exception {
        try {
            log.info("📄 Generando PDF para: {}", paymentEvent.userName());

            // Cargar el reporte compilado
            InputStream jasperStream = new ClassPathResource("reports/payment_receipt.jasper").getInputStream();

            // USAR TUS DATOS REALES - SIN DATOS DE PRUEBA
            Map<String, Object> parameters = createReportParameters(paymentEvent);
            List<PaymentReceiptData> receiptDataList = createReceiptDataList(paymentEvent);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(receiptDataList);

            // Generar reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, parameters, dataSource);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            log.info("✅ PDF generado: {} bytes para {}", pdfBytes.length, paymentEvent.userName());
            return pdfBytes;

        } catch (Exception e) {
            log.error("❌ Error generando PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private Map<String, Object> createReportParameters(PaymentApprovedEvent paymentEvent) {
        Map<String, Object> parameters = new HashMap<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // USAR TODOS TUS DATOS REALES
        parameters.put("companyName", "FitDesk");
        parameters.put("reportTitle", "COMPROBANTE DE PAGO");
        parameters.put("userName", paymentEvent.userName());
        parameters.put("userEmail", paymentEvent.userEmail());
        parameters.put("transactionId", paymentEvent.mercadoPagoTransactionId());
        parameters.put("paymentDate", paymentEvent.paymentDate().format(dateTimeFormatter));
        parameters.put("amount", paymentEvent.amount());
        parameters.put("planStartDate", paymentEvent.paymentDate().toLocalDate().format(dateFormatter));
        parameters.put("planEndDate",
                paymentEvent.paymentDate().toLocalDate().plusMonths(paymentEvent.durationMonths()).format(dateFormatter));
        parameters.put("externalReference", paymentEvent.externalReference());
        parameters.put("paymentId", paymentEvent.paymentId().toString());

        return parameters;
    }

    private List<PaymentReceiptData> createReceiptDataList(PaymentApprovedEvent paymentEvent) {
        List<PaymentReceiptData> dataList = new ArrayList<>();

        // USAR TUS DATOS REALES DEL EVENTO
        PaymentReceiptData receiptData = PaymentReceiptData.builder()
                .itemDescription(paymentEvent.planName()) // TU PLAN REAL
                .duration(paymentEvent.durationMonths() + " mes(es)") // TU DURACIÓN REAL
                .unitPrice(paymentEvent.amount()) // TU PRECIO REAL
                .quantity(1)
                .totalAmount(paymentEvent.amount()) // TU TOTAL REAL
                .build();

        dataList.add(receiptData);
        return dataList;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentReceiptData {
        private String itemDescription;
        private String duration;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal totalAmount;
    }
}