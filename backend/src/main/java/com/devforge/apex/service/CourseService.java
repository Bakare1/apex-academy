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

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}