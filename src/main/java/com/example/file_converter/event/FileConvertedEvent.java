package com.example.file_converter.event;

import java.util.List;

public record FileConvertedEvent(
        String eventId,
        String eventType,
        String sagaId,
        String causationId,
        String occurredAt,
        Payload payload
) {
    public record Payload(String sourceBucket, String sourceObjectKey, List<Output> outputs) {
    }

    public record Output(String bucket, String objectKey) {
    }
}