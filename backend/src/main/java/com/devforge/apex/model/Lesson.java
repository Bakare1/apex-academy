package com.devforge.apex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lessons")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId; // Links lesson to a specific course

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content; // Markdown text or description

    private String videoUrl; // Video streaming link

    @Column(nullable = false)
    private Integer sequenceOrder; // Order of the lesson in the course
}