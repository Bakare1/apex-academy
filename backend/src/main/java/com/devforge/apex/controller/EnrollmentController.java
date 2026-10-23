package com.devforge.apex.controller;

import com.devforge.apex.dto.EnrolledCourseResponse;
import com.devforge.apex.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrolledCourseResponse>> getMyEnrolledCourses(Authentication authentication) {
        List<EnrolledCourseResponse> enrolledCourses = enrollmentService.getStudentEnrolledCourses(authentication);
        return ResponseEntity.ok(enrolledCourses);
    }
}