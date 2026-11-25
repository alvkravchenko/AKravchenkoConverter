package com.example.file_converter.event;

public record FileUploadedEvent(
        String eventId,
        String eventType,
        String sagaId,
        String occurredAt,
        Payload payload
) {
    public record Payload(String bucket, String objectKey) {
    }
}