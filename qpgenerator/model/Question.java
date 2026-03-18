package com.miniproject.qpgenerator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(nullable = false)
    private Integer unit; // 1-5

    @Column(nullable = false)
    private Integer marks; // 2, 5, 7, 8, 10, 15, 16

    @Column(name = "bloom_level", nullable = false)
    private Integer bloomLevel; // 1-4 (Remember, Understand, Apply, Analyze)

    @Builder.Default
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(name = "faculty_id")
    private Long facultyId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
