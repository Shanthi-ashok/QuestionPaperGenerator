package com.miniproject.qpgenerator.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String subjectCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;
}
