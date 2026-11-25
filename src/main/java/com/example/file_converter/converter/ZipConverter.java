package com.example.file_converter.converter;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipConverter implements Converter {

    private final ObjectProvider<ConverterResolver> resolverProvider;

    public ZipConverter(ObjectProvider<ConverterResolver> resolverProvider) {
        this.resolverProvider = resolverProvider;
    }

    @Override
    public boolean supports(String fileType) {
        return "zip".equals(fileType);
    }

    @Override
    public List<ConversionOutput> convert(byte[] source, String sourceKey) throws IOException {
        ConverterResolver resolver = resolverProvider.getObject();
        List<ConversionOutput> outputs = new ArrayList<>();

        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(source))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                String entryName = entry.getName();
                if (entryName.contains("..") || entryName.startsWith("/")) {
                    throw new IOException("Недопустимое имя файла в архиве: " + entryName);
                }

                String entryKey = sourceKey + "/" + entryName;

                Converter converter = resolver.resolve(entryKey)
                        .orElseThrow(() -> new IOException("Нет конвертера для файла " + entryKey));

                byte[] entryBytes = zip.readAllBytes();
                outputs.addAll(converter.convert(entryBytes, entryKey));
            }
        }
        return outputs;
    }
}