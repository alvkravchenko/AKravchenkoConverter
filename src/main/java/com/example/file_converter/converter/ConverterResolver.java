package com.example.file_converter.converter;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConverterResolver {

    private final List<Converter> converters;

    public ConverterResolver(List<Converter> converters) {
        this.converters = converters;
    }

    private String fileTypeOf(String objectKey) {
        int dotIndex = objectKey.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new IllegalArgumentException("Не удалось определить тип файла: " + objectKey);
        }
        return objectKey.substring(dotIndex + 1).toLowerCase();
    }

    public Optional<Converter> resolve(String objectKey) {
        String fileType = fileTypeOf(objectKey);
        for (Converter converter : converters) {
            if (converter.supports(fileType)) {
                return Optional.of(converter);
            }
        }
        return Optional.empty();
    }
}
