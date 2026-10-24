package com.devforge.apex.service;

import com.devforge.apex.model.Lesson;
import com.devforge.apex.repo.EnrollmentRepository;
import com.devforge.apex.repo.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<Lesson> getLessonsForCourse(String studentEmail, Long courseId) {
        // Enforce secure access: verify student is enrolled in the course
        boolean isEnrolled = enrollmentRepository.existsByStudentEmailAndCourseId(studentEmail, courseId);

        if (!isEnrolled) {
            throw new AccessDeniedException("Access Denied: You must be enrolled in this course to view its lessons.");
        }

        return lessonRepository.findByCourseIdOrderBySequenceOrderAsc(courseId);
    }

    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }
}