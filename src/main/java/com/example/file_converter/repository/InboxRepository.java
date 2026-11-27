package com.example.file_converter.repository;

import com.example.file_converter.entity.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxRepository extends JpaRepository<Inbox, UUID> {
}
