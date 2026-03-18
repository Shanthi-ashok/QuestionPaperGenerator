package com.miniproject.qpgenerator.question.repository;

import com.miniproject.qpgenerator.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Part A: Find by subject, unit, marks, bloom level, and status
    @Query("SELECT q FROM Question q WHERE q.subjectCode = :subjectCode AND q.unit = :unit AND q.marks = :marks AND q.bloomLevel IN :bloomLevels AND q.status = 'ACTIVE' ORDER BY q.usageCount ASC")
    List<Question> findBySubjectCodeAndUnitAndMarksAndBloomLevelsAndStatus(
            @Param("subjectCode") String subjectCode,
            @Param("unit") Integer unit,
            @Param("marks") Integer marks,
            @Param("bloomLevels") List<Integer> bloomLevels);

    // Part B: Find questions by subject, unit, exact bloom level, marks, and status
    @Query("SELECT q FROM Question q WHERE q.subjectCode = :subjectCode AND q.unit = :unit AND q.bloomLevel = :bloomLevel AND q.marks = :marks AND q.status = 'ACTIVE' ORDER BY q.usageCount ASC")
    List<Question> findBySubjectCodeAndUnitAndExactBloomLevelAndMarksAndStatus(
            @Param("subjectCode") String subjectCode,
            @Param("unit") Integer unit,
            @Param("bloomLevel") Integer bloomLevel,
            @Param("marks") Integer marks);

    // Part B: Find questions by subject, unit, bloom level <= specified, marks, and status
    @Query("SELECT q FROM Question q WHERE q.subjectCode = :subjectCode AND q.unit = :unit AND q.bloomLevel <= :bloomLevel AND q.marks = :marks AND q.status = 'ACTIVE' ORDER BY q.usageCount ASC")
    List<Question> findBySubjectCodeAndUnitAndBloomLevelMaxAndMarksAndStatus(
            @Param("subjectCode") String subjectCode,
            @Param("unit") Integer unit,
            @Param("bloomLevel") Integer bloomLevel,
            @Param("marks") Integer marks);

    // Check if any question exists at exact bloom level
    @Query("SELECT COUNT(q) > 0 FROM Question q WHERE q.subjectCode = :subjectCode AND q.unit = :unit AND q.bloomLevel = :bloomLevel AND q.marks = :marks AND q.status = 'ACTIVE'")
    boolean existsBySubjectCodeAndUnitAndExactBloomLevelAndMarksAndStatus(
            @Param("subjectCode") String subjectCode,
            @Param("unit") Integer unit,
            @Param("bloomLevel") Integer bloomLevel,
            @Param("marks") Integer marks);

    // Legacy methods for backward compatibility
    List<Question> findBySubjectCodeAndUnitAndMarksAndStatusOrderByUsageCountAsc(String subjectCode, Integer unit, Integer marks, String status);

    @Query("SELECT q FROM Question q WHERE q.subjectCode = :subjectCode AND q.unit IN :units AND q.marks = :marks AND q.status = 'ACTIVE' ORDER BY q.usageCount ASC")
    List<Question> findBySubjectCodeAndUnitsAndMarks(@Param("subjectCode") String subjectCode, @Param("units") List<Integer> units, @Param("marks") Integer marks);

    @Query("SELECT q FROM Question q WHERE q.subjectCode = :subjectCode AND q.unit = :unit AND q.bloomLevel <= :bloomLevel AND q.status = 'ACTIVE' ORDER BY q.usageCount ASC")
    List<Question> findBySubjectCodeAndUnitAndBloomLevelMax(@Param("subjectCode") String subjectCode, @Param("unit") Integer unit, @Param("bloomLevel") Integer bloomLevel);

    long countBySubjectCodeAndStatus(String subjectCode, String status);
}
