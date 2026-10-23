package com.devforge.apex.service;

import com.devforge.apex.dto.EnrolledCourseResponse;
import com.devforge.apex.model.Course;
import com.devforge.apex.model.Enrollment;
import com.devforge.apex.repo.CourseRepository;
import com.devforge.apex.repo.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public boolean isStudentEnrolled(String studentEmail, Long courseId) {
        return enrollmentRepository.existsByStudentEmailAndCourseId(studentEmail, courseId);
    }

    public List<EnrolledCourseResponse> getStudentEnrolledCourses(Authentication authentication) {
        String studentEmail = authentication.getName();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentEmail(studentEmail);

        return enrollments.stream().map(enrollment -> {
            Course course = courseRepository.findById(enrollment.getCourseId())
                    .orElse(null);

            return EnrolledCourseResponse.builder()
                    .enrollmentId(enrollment.getId())
                    .courseId(enrollment.getCourseId())
                    .courseTitle(course != null ? course.getTitle() : "N/A")
                    .courseDescription(course != null ? course.getDescription() : "N/A")
                    .price(0.0) // Default since price field isn't in Course entity yet
                    .instructorName(course != null ? course.getInstructorEmail() : "DevForge")
                    .enrolledAt(enrollment.getEnrolledAt())
                    .build();
        }).collect(Collectors.toList());
    }
}