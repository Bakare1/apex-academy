package com.devforge.apex.service;

import com.devforge.apex.dto.LiveSessionRequest;
import com.devforge.apex.model.Course;
import com.devforge.apex.model.LiveSession;
import com.devforge.apex.repo.CourseRepository;
import com.devforge.apex.repo.LiveSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveSessionService {

    private final LiveSessionRepository liveSessionRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<LiveSession> getSessionsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course with ID " + courseId + " not found");
        }
        return liveSessionRepository.findByCourseId(courseId);
    }

    public LiveSession scheduleSession(Long courseId, LiveSessionRequest request, Authentication authentication) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course with ID " + courseId + " not found"));

        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Enforce ownership: Only course owner instructor or admin can schedule live classes
        if (!isAdmin && !course.getInstructorEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("Access Denied: You can only schedule live sessions for your own courses.");
        }

        LiveSession session = LiveSession.builder()
                .title(request.title())
                .description(request.description())
                .scheduledTime(request.scheduledTime())
                .roomUrlOrToken(request.roomUrlOrToken())
                .course(course)
                .instructorEmail(currentUserEmail)
                .build();

        return liveSessionRepository.save(session);
    }

    public void deleteSession(Long sessionId, Authentication authentication) {
        LiveSession session = liveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Live session with ID " + sessionId + " not found"));

        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !session.getInstructorEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("Access Denied: You can only delete your own live sessions.");
        }

        liveSessionRepository.delete(session);
    }
}