package com.example.file_converter.service;

import com.example.file_converter.converter.ConversionOutput;
import com.example.file_converter.entity.Inbox;
import com.example.file_converter.entity.Outbox;
import com.example.file_converter.event.FileConvertedEvent;
import com.example.file_converter.event.FileUploadedEvent;
import com.example.file_converter.repository.InboxRepository;
import com.example.file_converter.repository.OutboxRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ResultWriter {

    private static final String RESULT_TOPIC = "file.converted";

    private final InboxRepository inboxRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final String outputBucket;

    public ResultWriter(InboxRepository inboxRepository,
                        OutboxRepository outboxRepository,
                        ObjectMapper objectMapper,
                        @Value("${minio.bucket.output}") String outputBucket) {
        this.inboxRepository = inboxRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.outputBucket = outputBucket;
    }

    @Transactional
    public void writeSuccess(FileUploadedEvent event, List<ConversionOutput> pdfs) {
        Inbox inbox = new Inbox();
        inbox.setEventId(UUID.fromString(event.eventId()));
        inbox.setProcessedAt(Instant.now());
        inboxRepository.save(inbox);

        UUID outgoingEventId = UUID.randomUUID();
        FileConvertedEvent outgoing = new FileConvertedEvent(
                outgoingEventId.toString(),
                "FileConverted",
                event.sagaId(),
                event.eventId(),
                Instant.now().toString(),
                new FileConvertedEvent.Payload(
                        event.payload().bucket(),
                        event.payload().objectKey(),
                        pdfs.stream()
                                .map(pdf -> new FileConvertedEvent.Output(outputBucket, pdf.objectKey()))
                                .toList())
        );

        Outbox outbox = new Outbox();
        outbox.setId(outgoingEventId);
        outbox.setTopic(RESULT_TOPIC);
        outbox.setPayload(toJson(outgoing));
        outboxRepository.save(outbox);
    }

    private String toJson(FileConvertedEvent event) {
        return objectMapper.writeValueAsString(event);
    }
}