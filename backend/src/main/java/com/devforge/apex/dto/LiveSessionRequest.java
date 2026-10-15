package com.devforge.apex.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record LiveSessionRequest(
        @NotBlank(message = "Live session title is required")
        String title,

        String description,

        @NotNull(message = "Scheduled time is required")
        @Future(message = "Live session must be scheduled in the future")
        LocalDateTime scheduledTime,

        @NotBlank(message = "Room URL or video token is required")
        String roomUrlOrToken
) {}