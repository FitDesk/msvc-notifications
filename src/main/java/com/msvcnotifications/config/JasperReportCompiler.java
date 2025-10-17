package com.msvcnotifications.config;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@Slf4j
public class JasperReportCompiler implements CommandLineRunner {

    @Value("${app.jasper.auto-compile:true}")
    private boolean autoCompile;

    @Override
    public void run(String... args) throws Exception {
        if (autoCompile) {
            compileReports();
        }
    }

    private void compileReports() {
        try {
            log.info("🔨 Compilando reportes JasperReports...");

            compileReport("reports/payment_receipt.jrxml", "reports/payment_receipt.jasper");

            log.info("✅ Reportes compilados exitosamente");

        } catch (Exception e) {
            log.error("❌ Error compilando reportes: {}", e.getMessage(), e);
        }
    }

    private void compileReport(String sourcePath, String targetPath) {
        try {
            ClassPathResource sourceResource = new ClassPathResource(sourcePath);

            if (!sourceResource.exists()) {
                log.warn("⚠️ Archivo fuente no encontrado: {}", sourcePath);
                return;
            }

            String resourcesPath = "src/main/resources/";
            Path targetFilePath = Paths.get(resourcesPath + targetPath);

            Files.createDirectories(targetFilePath.getParent());

            try (InputStream sourceStream = sourceResource.getInputStream()) {
                // Compilar el reporte desde InputStream
                JasperReport compiledReport = JasperCompileManager.compileReport(sourceStream);

                // Guardar el reporte compilado usando JRSaver
                JRSaver.saveObject(compiledReport, targetFilePath.toString());

                log.info("📄 Reporte compilado: {}", targetPath);
            }

        } catch (Exception e) {
            log.error("❌ Error compilando {}: {}", sourcePath, e.getMessage());
        }
    }
}