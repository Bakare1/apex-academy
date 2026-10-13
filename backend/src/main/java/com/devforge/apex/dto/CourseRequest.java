package com.devforge.apex.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(
        @NotBlank(message = "Course title is required")
        String title,

        String description
) {}