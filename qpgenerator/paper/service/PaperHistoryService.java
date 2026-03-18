package com.miniproject.qpgenerator.paper.service;

import com.miniproject.qpgenerator.model.Paper;
import com.miniproject.qpgenerator.model.PaperQuestion;
import com.miniproject.qpgenerator.model.Question;
import com.miniproject.qpgenerator.paper.dto.PaperDetailResponse;
import com.miniproject.qpgenerator.paper.dto.PaperQuestionDetailResponse;
import com.miniproject.qpgenerator.paper.dto.QuestionResponse;
import com.miniproject.qpgenerator.paper.repository.PaperQuestionRepository;
import com.miniproject.qpgenerator.paper.repository.PaperRepository;
import com.miniproject.qpgenerator.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaperHistoryService {

    private final PaperRepository paperRepository;
    private final PaperQuestionRepository paperQuestionRepository;
    private final QuestionRepository questionRepository;

    /**
     * Save paper and its questions to database
     */
    public void savePaperWithQuestions(Long paperId, Integer setNo, List<QuestionResponse> partA, List<QuestionResponse> partB) {
        log.info("Saving paper {} set {} with questions", paperId, setNo);

        // Save Part A questions
        for (QuestionResponse q : partA) {
            saveSingleQuestion(paperId, setNo, q, "A");
        }

        // Save Part B questions
        for (QuestionResponse q : partB) {
            saveSingleQuestion(paperId, setNo, q, "B");
        }

        log.info("Saved {} questions for paper {} set {}", partA.size() + partB.size(), paperId, setNo);
    }

    /**
     * Save a single question for a paper
     */
    private void saveSingleQuestion(Long paperId, Integer setNo, QuestionResponse q, String part) {
        PaperQuestion paperQuestion = PaperQuestion.builder()
                .paperId(paperId)
                .setNo(setNo)
                .questionId(q.getQuestionId())
                .part(part)
                .subPart(q.getSubPart())
                .orderIndex(q.getOrderIndex())
                .build();

        paperQuestionRepository.save(paperQuestion);
    }

    /**
     * Get all papers by subject code
     */
    public List<PaperDetailResponse> getPapersBySubject(String subjectCode) {
        log.info("Fetching papers for subject: {}", subjectCode);
        return paperRepository.findBySubjectCodeOrderByCreatedAtDesc(subjectCode)
                .stream()
                .map(this::mapToPaperDetail)
                .collect(Collectors.toList());
    }

    /**
     * Get papers by subject and exam type
     */
    public List<PaperDetailResponse> getPapersBySubjectAndExamType(String subjectCode, String examType) {
        log.info("Fetching papers for subject: {} and examType: {}", subjectCode, examType);
        return paperRepository.findBySubjectCodeAndExamTypeOrderByCreatedAtDesc(subjectCode, examType)
                .stream()
                .map(this::mapToPaperDetail)
                .collect(Collectors.toList());
    }

    /**
     * Get papers by faculty
     */
    public List<PaperDetailResponse> getPapersByFaculty(Long facultyId) {
        log.info("Fetching papers for faculty: {}", facultyId);
        return paperRepository.findByFacultyIdOrderByCreatedAtDesc(facultyId)
                .stream()
                .map(this::mapToPaperDetail)
                .collect(Collectors.toList());
    }

    /**
     * Get papers by faculty and subject
     */
    public List<PaperDetailResponse> getPapersByFacultyAndSubject(Long facultyId, String subjectCode) {
        log.info("Fetching papers for faculty: {} and subject: {}", facultyId, subjectCode);
        return paperRepository.findByFacultyIdAndSubjectCodeOrderByCreatedAtDesc(facultyId, subjectCode)
                .stream()
                .map(this::mapToPaperDetail)
                .collect(Collectors.toList());
    }

    /**
     * Get papers by faculty, subject, and exam type
     */
    public List<PaperDetailResponse> getPapersByFacultySubjectAndExamType(Long facultyId, String subjectCode, String examType) {
        log.info("Fetching papers for faculty: {}, subject: {}, examType: {}", facultyId, subjectCode, examType);
        return paperRepository.findByFacultyIdAndSubjectCodeAndExamTypeOrderByCreatedAtDesc(facultyId, subjectCode, examType)
                .stream()
                .map(this::mapToPaperDetail)
                .collect(Collectors.toList());
    }

    /**
     * Get single paper detail by ID with all its questions
     */
    public Map<String, Object> getPaperById(Long paperId) {
        log.info("Fetching paper detail: {}", paperId);

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found: " + paperId));

        Map<String, Object> result = new HashMap<>();
        result.put("paper", mapToPaperDetail(paper));

        // Get all questions for this paper grouped by set
        List<PaperQuestion> allPaperQuestions = paperQuestionRepository.findByPaperId(paperId);
        
        // Group by set number
        Map<Integer, Map<String, List<PaperQuestionDetailResponse>>> questionsBySet = new TreeMap<>();
        
        for (PaperQuestion pq : allPaperQuestions) {
            Question q = questionRepository.findById(pq.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + pq.getQuestionId()));

            PaperQuestionDetailResponse qDetail = mapToPaperQuestionDetail(pq, q);

            questionsBySet
                    .computeIfAbsent(pq.getSetNo(), k -> new HashMap<>())
                    .computeIfAbsent(pq.getPart(), k -> new ArrayList<>())
                    .add(qDetail);
        }

        result.put("sets", questionsBySet);
        return result;
    }

    /**
     * Get questions for a specific set and part
     */
    public List<PaperQuestionDetailResponse> getPaperSetQuestions(Long paperId, Integer setNo, String part) {
        log.info("Fetching questions for paper {} set {} part {}", paperId, setNo, part);

        List<PaperQuestion> paperQuestions = part != null
                ? paperQuestionRepository.findByPaperIdAndSetNoAndPart(paperId, setNo, part)
                : paperQuestionRepository.findByPaperIdAndSetNo(paperId, setNo);

        return paperQuestions.stream()
                .map(pq -> {
                    Question q = questionRepository.findById(pq.getQuestionId())
                            .orElseThrow(() -> new RuntimeException("Question not found: " + pq.getQuestionId()));
                    return mapToPaperQuestionDetail(pq, q);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get Part A questions for a paper set
     */
    public List<PaperQuestionDetailResponse> getPaperSetPartA(Long paperId, Integer setNo) {
        return getPaperSetQuestions(paperId, setNo, "A");
    }

    /**
     * Get Part B questions for a paper set
     */
    public List<PaperQuestionDetailResponse> getPaperSetPartB(Long paperId, Integer setNo) {
        return getPaperSetQuestions(paperId, setNo, "B");
    }

    /**
     * Delete paper and all its questions
     */
    public void deletePaper(Long paperId) {
        log.info("Deleting paper and questions: {}", paperId);

        if (!paperRepository.existsById(paperId)) {
            throw new RuntimeException("Paper not found: " + paperId);
        }

        paperQuestionRepository.deleteByPaperId(paperId);
        paperRepository.deleteById(paperId);
        log.info("Paper deleted successfully: {}", paperId);
    }

    /**
     * Map Paper entity to PaperDetailResponse DTO
     */
    private PaperDetailResponse mapToPaperDetail(Paper paper) {
        return PaperDetailResponse.builder()
                .paperId(paper.getId())
                .subjectCode(paper.getSubjectCode())
                .examType(paper.getExamType())
                .date(paper.getDate())
                .time(paper.getTime())
                .yearSem(paper.getYearSem())
                .setsCount(paper.getSetsCount())
                .facultyId(paper.getFacultyId())
                .createdAt(paper.getCreatedAt())
                .build();
    }

    /**
     * Map PaperQuestion + Question to PaperQuestionDetailResponse DTO
     */
    private PaperQuestionDetailResponse mapToPaperQuestionDetail(PaperQuestion pq, Question q) {
        return PaperQuestionDetailResponse.builder()
                .questionId(q.getId())
                .questionText(q.getQuestionText())
                .part(pq.getPart())
                .subPart(pq.getSubPart())
                .orderIndex(pq.getOrderIndex())
                .unit(q.getUnit())
                .marks(q.getMarks())
                .bloomLevel(q.getBloomLevel())
                .build();
    }
}
