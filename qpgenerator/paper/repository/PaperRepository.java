package com.miniproject.qpgenerator.paper.repository;

import com.miniproject.qpgenerator.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    
    // Get papers by subject and exam type
    List<Paper> findBySubjectCodeAndExamTypeOrderByCreatedAtDesc(String subjectCode, String examType);

    // Get all papers by subject code
    List<Paper> findBySubjectCodeOrderByCreatedAtDesc(String subjectCode);

    // Get all papers by faculty
    List<Paper> findByFacultyIdOrderByCreatedAtDesc(Long facultyId);

    // Get all papers by faculty and subject
    List<Paper> findByFacultyIdAndSubjectCodeOrderByCreatedAtDesc(Long facultyId, String subjectCode);

    // Get all papers by faculty, subject, and exam type
    List<Paper> findByFacultyIdAndSubjectCodeAndExamTypeOrderByCreatedAtDesc(Long facultyId, String subjectCode, String examType);
}
