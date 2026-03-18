package com.miniproject.qpgenerator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "papers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode;

    @Column(nullable = false, length = 20)
    private String examType; // INTERNAL1, INTERNAL2, MODEL

    private String date;
    private String time;
    private String yearSem;

    @Column(name = "sets_count", nullable = false)
    private Integer setsCount;

    @Column(name = "faculty_id")
    private Long facultyId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
