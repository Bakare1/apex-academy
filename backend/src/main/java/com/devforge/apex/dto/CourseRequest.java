package com.devforge.apex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseRequest(
        @NotBlank(message = "Course title is required")
        String title,

        String description,

        @NotNull(message = "Course price is required")
        Double price
) {}