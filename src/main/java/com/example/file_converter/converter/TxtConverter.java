package com.example.file_converter.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class TxtConverter implements Converter {

    private static final float MARGIN_X = 50;
    private static final float START_Y = 750;
    private static final float BOTTOM_MARGIN = 50;
    private static final float LINE_HEIGHT = 14;
    private static final float FONT_SIZE = 12;

    @Override
    public boolean supports(String fileType) {
        return "txt".equals(fileType);
    }

    @Override
    public byte[] convert(byte[] source, String sourceKey) throws IOException {
        String text = new String(source, StandardCharsets.UTF_8);

        try (PDDocument document = new PDDocument()) {
            PDType0Font font = PDType0Font.load(document, fontStream());
            writeLines(document, font, text.split("\\R"));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private void writeLines(PDDocument document, PDType0Font font, String[] lines) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);
        try {
            content.beginText();
            content.setFont(font, FONT_SIZE);
            float y = START_Y;
            content.newLineAtOffset(MARGIN_X, y);

            for (String line : lines) {
                if (y < BOTTOM_MARGIN) {
                    content.endText();
                    content.close();

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    content.beginText();
                    content.setFont(font, FONT_SIZE);
                    y = START_Y;
                    content.newLineAtOffset(MARGIN_X, y);
                }

                content.showText(line);
                content.newLineAtOffset(0, -LINE_HEIGHT);
                y -= LINE_HEIGHT;
            }

            content.endText();
        } finally {
            content.close();
        }
    }

    private InputStream fontStream() {
        InputStream stream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf");
        if (stream == null) {
            throw new IllegalStateException("Шрифт не найден: /fonts/DejaVuSans.ttf");
        }
        return stream;
    }
}