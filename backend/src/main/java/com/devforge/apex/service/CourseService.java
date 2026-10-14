package com.devforge.apex.service;

import com.devforge.apex.dto.CourseRequest;
import com.devforge.apex.model.Course;
import com.devforge.apex.repo.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course createCourse(CourseRequest request, Authentication authentication) {
        String instructorEmail = authentication.getName(); // Extracts email from JWT subject

        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .instructorEmail(instructorEmail)
                .build();

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id, Authentication authentication) {
        // 1. Handle 404: Check if course exists
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with ID " + id + " not found"));

        String currentUserEmail = authentication.getName();

        // 2. Check if the current user is an ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // 3. Handle 403: If instructor, ensure they own the course
        if (!isAdmin && !course.getInstructorEmail().equals(currentUserEmail)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Access Denied: You can only delete courses that you created."
            );
        }

        // 4. Perform secure deletion
        courseRepository.delete(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}