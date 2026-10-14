package com.devforge.apex.controller;

import com.devforge.apex.dto.CourseRequest;
import com.devforge.apex.model.Course;
import com.devforge.apex.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // Returns all saved courses from the database
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // Restricted to instructors - saves a new course to the database
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Course> createCourse(
            @Valid @RequestBody CourseRequest request,
            Authentication authentication
    ) {
        Course createdCourse = courseService.createCourse(request, authentication);
        return ResponseEntity.ok(createdCourse);
    }

    // Restricted to users with ROLE_ADMIN or ROLE_INSTRUCTOR with secure ownership validation
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long id,
            Authentication authentication
    ) {
        courseService.deleteCourse(id, authentication);
        return ResponseEntity.ok("Course with ID " + id + " deleted successfully!");
    }
}