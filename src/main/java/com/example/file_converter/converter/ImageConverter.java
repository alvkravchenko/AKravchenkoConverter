package com.example.file_converter.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

@Component
public class ImageConverter implements Converter {

    private static final Set<String> SUPPORTED = Set.of("png", "jpg", "jpeg");
    private static final float MARGIN = 50;

    @Override
    public boolean supports(String fileType) {
        return fileType != null && SUPPORTED.contains(fileType);
    }

    @Override
    public byte[] convert(byte[] source, String sourceKey) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDImageXObject image = PDImageXObject.createFromByteArray(document, source, "image");

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float maxWidth = pageWidth - 2 * MARGIN;
            float maxHeight = pageHeight - 2 * MARGIN;

            float scale = Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());
            float width = image.getWidth() * scale;
            float height = image.getHeight() * scale;

            float x = (pageWidth - width) / 2;
            float y = (pageHeight - height) / 2;

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.drawImage(image, x, y, width, height);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}