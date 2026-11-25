package com.example.file_converter.converter;

public interface Converter {

    boolean supports(String fileType);

    byte[] convert(byte[] source, String sourceKey) throws Exception;
}
