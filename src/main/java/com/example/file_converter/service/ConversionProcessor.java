package com.example.file_converter.service;

import com.example.file_converter.converter.ConversionOutput;
import com.example.file_converter.converter.Converter;
import com.example.file_converter.converter.ConverterResolver;
import com.example.file_converter.event.FileUploadedEvent;
import com.example.file_converter.repository.InboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ConversionProcessor {

    private final MinioStorageService storage;
    private final ConverterResolver resolver;
    private final InboxRepository inboxRepository;
    private final ResultWriter resultWriter;
    private final String outputBucket;

    public ConversionProcessor(MinioStorageService storage,
                               ConverterResolver resolver,
                               InboxRepository inboxRepository,
                               ResultWriter resultWriter,
                               @Value("${minio.bucket.output}") String outputBucket) {
        this.storage = storage;
        this.resolver = resolver;
        this.inboxRepository = inboxRepository;
        this.resultWriter = resultWriter;
        this.outputBucket = outputBucket;
    }

    public void process(FileUploadedEvent event) throws Exception {
        UUID eventId = UUID.fromString(event.eventId());

        if (inboxRepository.existsById(eventId)) {
            log.info("Событие {} уже обработано — пропускаем", eventId);
            return;
        }

        String sourceBucket = event.payload().bucket();
        String sourceKey = event.payload().objectKey();

        Converter converter = resolver.resolve(sourceKey)
                .orElseThrow(() -> new IllegalArgumentException("Нет конвертера для файла " + sourceKey));

        byte[] source = storage.download(sourceBucket, sourceKey);
        List<ConversionOutput> pdfs = converter.convert(source, sourceKey);
        for (ConversionOutput pdf : pdfs) {
            storage.upload(outputBucket, pdf.objectKey(), pdf.content(), "application/pdf");
        }

        resultWriter.writeSuccess(event, pdfs);

        log.info("Файл {} сконвертирован, PDF: {}", sourceKey,
                pdfs.stream().map(ConversionOutput::objectKey).toList());
    }
}