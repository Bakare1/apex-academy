package com.devforge.apex.controller;

import com.devforge.apex.model.Lesson;
import com.devforge.apex.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    // Students can view lessons only if enrolled
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Lesson>> getCourseLessons(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        String studentEmail = authentication.getName();
        List<Lesson> lessons = lessonService.getLessonsForCourse(studentEmail, courseId);
        return ResponseEntity.ok(lessons);
    }

    // Instructors/Admins can create lessons
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        Lesson createdLesson = lessonService.createLesson(lesson);
        return ResponseEntity.ok(createdLesson);
    }
}