package com.devforge.apex.dto;

import com.devforge.apex.model.LiveSession;

import java.time.LocalDateTime;

public record LiveSessionResponse(
        Long id,
        String title,
        String description,
        LocalDateTime scheduledTime,
        String roomUrlOrToken,
        Long courseId,
        String instructorEmail
) {
    public static LiveSessionResponse fromEntity(LiveSession session) {
        return new LiveSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getScheduledTime(),
                session.getRoomUrlOrToken(),
                session.getCourse().getId(),
                session.getInstructorEmail()
        );
    }
}