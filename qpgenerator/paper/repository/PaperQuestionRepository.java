package com.miniproject.qpgenerator.paper.repository;

import com.miniproject.qpgenerator.model.PaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperQuestionRepository extends JpaRepository<PaperQuestion, Long> {

    // Get all questions for a specific paper set
    @Query("SELECT pq FROM PaperQuestion pq WHERE pq.paperId = :paperId AND pq.setNo = :setNo ORDER BY pq.part ASC, pq.orderIndex ASC")
    List<PaperQuestion> findByPaperIdAndSetNo(@Param("paperId") Long paperId, @Param("setNo") Integer setNo);

    // Get all questions for a specific paper
    @Query("SELECT pq FROM PaperQuestion pq WHERE pq.paperId = :paperId ORDER BY pq.setNo ASC, pq.part ASC, pq.orderIndex ASC")
    List<PaperQuestion> findByPaperId(@Param("paperId") Long paperId);

    // Get questions by part (A or B) for a paper set
    @Query("SELECT pq FROM PaperQuestion pq WHERE pq.paperId = :paperId AND pq.setNo = :setNo AND pq.part = :part ORDER BY pq.orderIndex ASC")
    List<PaperQuestion> findByPaperIdAndSetNoAndPart(
            @Param("paperId") Long paperId,
            @Param("setNo") Integer setNo,
            @Param("part") String part);

    // Delete all questions for a paper
    void deleteByPaperId(Long paperId);
}
