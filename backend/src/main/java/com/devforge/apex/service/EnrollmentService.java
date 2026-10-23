package com.devforge.apex.service;

import com.devforge.apex.model.Enrollment;
import com.devforge.apex.repo.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public List<Enrollment> getStudentEnrollments(Authentication authentication) {
        // Extract the student's email/username from the security principal/authentication
        String studentEmail = authentication.getName();
        return enrollmentRepository.findByStudentEmail(studentEmail);
    }

    public boolean isStudentEnrolled(String studentEmail, Long courseId) {
        return enrollmentRepository.existsByStudentEmailAndCourseId(studentEmail, courseId);
    }
}