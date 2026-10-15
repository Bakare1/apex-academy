package com.devforge.apex.controller;

import com.devforge.apex.dto.LiveSessionRequest;
import com.devforge.apex.dto.LiveSessionResponse;
import com.devforge.apex.model.LiveSession;
import com.devforge.apex.service.LiveSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/live-sessions")
@RequiredArgsConstructor
public class LiveSessionController {

    private final LiveSessionService liveSessionService;

    // Public or Enrolled students & instructors can view scheduled live sessions for a course
    @GetMapping
    public ResponseEntity<List<LiveSessionResponse>> getCourseSessions(@PathVariable Long courseId) {
        return ResponseEntity.ok(liveSessionService.getSessionsByCourse(courseId));
    }

    // Restricted to instructors and admins with ownership validation
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<LiveSession> scheduleSession(
            @PathVariable Long courseId,
            @Valid @RequestBody LiveSessionRequest request,
            Authentication authentication
    ) {
        LiveSession createdSession = liveSessionService.scheduleSession(courseId, request, authentication);
        return ResponseEntity.ok(createdSession);
    }

    // Delete a live session
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<String> deleteSession(
            @PathVariable Long courseId,
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        liveSessionService.deleteSession(sessionId, authentication);
        return ResponseEntity.ok("Live session deleted successfully!");
    }
}