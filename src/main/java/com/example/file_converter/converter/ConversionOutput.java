package com.example.file_converter.converter;

public record ConversionOutput(String objectKey, byte[] content) {

    public static String pdfKey(String sourceKey) {
        int dotIndex = sourceKey.lastIndexOf('.');
        String base = dotIndex < 0 ? sourceKey : sourceKey.substring(0, dotIndex);
        return base + ".pdf";
    }
}