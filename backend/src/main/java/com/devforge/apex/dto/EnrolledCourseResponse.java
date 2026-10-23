package com.devforge.apex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledCourseResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private Double price;
    private String instructorName;
    private LocalDateTime enrolledAt;
}