package com.miniproject.qpgenerator.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "paper_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(name = "set_no", nullable = false)
    private Integer setNo;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(nullable = false, length = 10)
    private String part; // A, B

    @Column(name = "sub_part", length = 5) // a, b, null
    private String subPart;

    @Column(name = "order_index")
    private Integer orderIndex; // Question position (1,2,3...)
}
