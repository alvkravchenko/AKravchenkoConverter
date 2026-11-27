package com.example.file_converter.converter;

import java.io.IOException;
import java.util.List;

public interface Converter {

    boolean supports(String fileType);

    List<ConversionOutput> convert(byte[] source, String sourceKey) throws IOException;
}
