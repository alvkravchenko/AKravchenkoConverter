package com.example.file_converter.listener;

import com.example.file_converter.event.FileUploadedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileUploadedListener {

    @KafkaListener(topics = "file.uploaded", groupId = "file-converter")
    public void listen(FileUploadedEvent event) {
        log.info("Получено событие: eventId={}, sagaId={}, файл={}/{}",
                event.eventId(), event.sagaId(),
                event.payload().bucket(), event.payload().objectKey());
    }
}