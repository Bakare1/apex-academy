package com.devforge.apex.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    // Public or accessible by any authenticated user (Student, Instructor, Admin)
    @GetMapping
    public ResponseEntity<String> getAllCourses() {
        return ResponseEntity.ok("Public/Authenticated: List of all courses");
    }

    // Restricted to users with ROLE_INSTRUCTOR
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<String> createCourse() {
        return ResponseEntity.ok("Instructor restricted: Course created successfully!");
    }

    // Restricted to users with ROLE_ADMIN or ROLE_INSTRUCTOR
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok("Admin/Instructor restricted: Course " + id + " deleted successfully!");
    }
}