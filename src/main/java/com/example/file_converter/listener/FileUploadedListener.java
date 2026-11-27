package com.example.file_converter.listener;

import com.example.file_converter.event.FileUploadedEvent;
import com.example.file_converter.service.ConversionProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileUploadedListener {

    private final ConversionProcessor processor;

    public FileUploadedListener(ConversionProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "file.uploaded", groupId = "file-converter")
    public void listen(FileUploadedEvent event, Acknowledgment ack) throws Exception {
        log.info("Получено событие: eventId={}, sagaId={}, файл={}/{}",
                event.eventId(), event.sagaId(),
                event.payload().bucket(), event.payload().objectKey());

        processor.process(event);
        ack.acknowledge();
    }
}