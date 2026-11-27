package com.example.file_converter.repository;

import com.example.file_converter.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

    List<Outbox> findByPublishedAtIsNullOrderByCreatedAt();
}