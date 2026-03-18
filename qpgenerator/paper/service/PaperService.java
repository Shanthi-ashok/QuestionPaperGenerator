package com.miniproject.qpgenerator.paper.service;

import com.miniproject.qpgenerator.model.*;
import com.miniproject.qpgenerator.paper.dto.*;
import com.miniproject.qpgenerator.paper.repository.PaperRepository;
import com.miniproject.qpgenerator.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaperService {

    private final PaperRepository paperRepository;
    private final QuestionRepository questionRepository;
    private final PaperHistoryService paperHistoryService;

    public PaperResponse generatePaper(GeneratePaperRequest request) {

        String subjectCode = request.getSubjectCode().toUpperCase();
        String examType = request.getExamType().toUpperCase();
        Integer setsCount = request.getNumberOfSets();

        log.info("Generating {} paper for {} with {} sets", examType, subjectCode, setsCount);

        Paper paper = Paper.builder()
                .subjectCode(subjectCode)
                .examType(examType)
                .date(request.getDate())
                .time(request.getTime())
                .yearSem(request.getYearSem())
                .setsCount(setsCount)
                .facultyId(1L)
                .build();

        Paper savedPaper = paperRepository.save(paper);

        List<SetResponse> sets = new ArrayList<>();
        Set<Long> globalUsedQuestionIds = new HashSet<>();

        for (int setNo = 1; setNo <= setsCount; setNo++) {

            Set<Long> setUsedIds = new HashSet<>(globalUsedQuestionIds);

            SetResponse setResponse =
                    generateSingleSet(subjectCode, examType,
                            request.getUnitBloomPreferences(),
                            setUsedIds);

            setResponse.setSetNumber(setNo);
            sets.add(setResponse);

            globalUsedQuestionIds.addAll(setUsedIds);
            savePaperQuestions(savedPaper.getId(), setNo, setResponse);
        }

        updateQuestionUsage(new ArrayList<>(globalUsedQuestionIds));

        return PaperResponse.builder()
                .paperId(savedPaper.getId())
                .subjectCode(subjectCode)
                .examType(examType)
                .sets(sets)
                .build();
    }

    private SetResponse generateSingleSet(String subjectCode,
                                          String examType,
                                          Map<Integer, Integer> bloomPrefs,
                                          Set<Long> usedQuestionIds) {

        List<QuestionResponse> partA =
                generatePartA(subjectCode, examType, usedQuestionIds);

        List<QuestionResponse> partB =
                generatePartB(subjectCode, examType, bloomPrefs, usedQuestionIds);

        return SetResponse.builder()
                .partA(partA)
                .partB(partB)
                .build();
    }

    // ==================== PART A ====================

    private List<QuestionResponse> generatePartA(String subjectCode,
                                                 String examType,
                                                 Set<Long> usedQuestionIds) {

        List<QuestionResponse> partA = new ArrayList<>();
        List<Integer> units = getPartAUnits(examType);

        for (Integer unit : units) {

            int questionsPerUnit = getQuestionsPerUnitPartA(examType);

            // CONSTRAINT: Part A must be 2 marks with ONLY bloom level 1 or 2
            List<Integer> allowedBloomLevels = Arrays.asList(1, 2);
            List<Question> candidates =
                    questionRepository
                            .findBySubjectCodeAndUnitAndMarksAndBloomLevelsAndStatus(
                                    subjectCode, unit, 2, allowedBloomLevels);

            log.info("Found {} candidates for Part A, Unit {}, Marks 2, Bloom Levels 1-2", candidates.size(), unit);

            Collections.shuffle(candidates);

            int added = 0;

            for (Question q : candidates) {
                if (!usedQuestionIds.contains(q.getId()) && added < questionsPerUnit) {

                    log.info("Selected Question {} for Part A: {} (Bloom Level: {})", 
                             q.getId(), q.getQuestionText(), q.getBloomLevel());
                    
                    partA.add(buildResponse(q, partA.size() + 1, null));
                    usedQuestionIds.add(q.getId());
                    added++;
                }
            }
        }
        return partA;
    }

    // ==================== PART B ====================

    private List<QuestionResponse> generatePartB(String subjectCode,
                                                 String examType,
                                                 Map<Integer, Integer> bloomPrefs,
                                                 Set<Long> usedQuestionIds) {

        List<QuestionResponse> partB = new ArrayList<>();
        List<Integer> units = getPartBUnits(examType);

        int baseOrderIndex = 11;

        for (int i = 0; i < units.size(); i++) {

            Integer unit = units.get(i);
            Integer bloomLevel = bloomPrefs.getOrDefault(unit, 3);
            int orderIndex = baseOrderIndex + i;

            // OPTION A
            List<QuestionResponse> optionA =
                    generateOption(subjectCode, unit, bloomLevel,
                            examType, usedQuestionIds,
                            orderIndex, "a");

            // OPTION B
            List<QuestionResponse> optionB =
                    generateOption(subjectCode, unit, bloomLevel,
                            examType, usedQuestionIds,
                            orderIndex, "b");

            partB.addAll(optionA);
            partB.addAll(optionB);
        }

        return partB;
    }

    // ==================== OPTION GENERATION (UPDATED) ====================

    private List<QuestionResponse> generateOption(String subjectCode,
                                                  Integer unit,
                                                  Integer bloomLevel,
                                                  String examType,
                                                  Set<Long> usedQuestionIds,
                                                  int orderIndex,
                                                  String optionLabel) {

        List<QuestionResponse> bestOption = new ArrayList<>();
        Set<Long> bestTempIds = new HashSet<>();
        long bestUsageSum = Long.MAX_VALUE;

        Integer fullMarks = "MODEL".equals(examType) ? 16 : 15;

        // ----- Candidate 1: single full-mark (15/16 treated same) -----
        {
            Set<Long> tempUsedIds = new HashSet<>();
            QuestionResponse full =
                    selectQuestion(subjectCode, unit, bloomLevel,
                            fullMarks, usedQuestionIds, tempUsedIds);

            if (full != null) {
                long usageSum = computeUsageSum(tempUsedIds);
                if (usageSum < bestUsageSum) {
                    bestUsageSum = usageSum;
                    bestOption.clear();
                    bestTempIds.clear();

                    full.setOrderIndex(orderIndex);
                    full.setSubPart(optionLabel);
                    bestOption.add(full);
                    bestTempIds.addAll(tempUsedIds);
                }
            }
        }

        // ----- Candidate 2: split combinations (10+5, 8+7, etc.) -----
        for (Integer[] split : getSplitCombinations(examType)) {

            Set<Long> tempUsedIds = new HashSet<>();

            QuestionResponse q1 =
                    selectQuestion(subjectCode, unit, bloomLevel,
                            split[0], usedQuestionIds, tempUsedIds);

            if (q1 == null) continue;

            QuestionResponse q2 =
                    selectQuestion(subjectCode, unit, bloomLevel,
                            split[1], usedQuestionIds, tempUsedIds);

            if (q2 == null) continue;

            long usageSum = computeUsageSum(tempUsedIds);
            if (usageSum < bestUsageSum) {
                bestUsageSum = usageSum;
                bestOption.clear();
                bestTempIds.clear();

                q1.setOrderIndex(orderIndex);
                q1.setSubPart(optionLabel + ".i");

                q2.setOrderIndex(orderIndex);
                q2.setSubPart(optionLabel + ".ii");

                bestOption.add(q1);
                bestOption.add(q2);
                bestTempIds.addAll(tempUsedIds);
            }
        }

        // If we found any candidate (single or split), accept best (lowest total usage)
        if (!bestOption.isEmpty()) {
            usedQuestionIds.addAll(bestTempIds);
        }

        // If still empty, fallback: no strict 15/16 requirement, just pick ANY highest mark available at/below bloom
        if (bestOption.isEmpty()) {
            Set<Long> tempUsedIds = new HashSet<>();
            List<Integer> fallbackMarks = getFallbackMarks(examType);
            for (Integer m : fallbackMarks) {
                QuestionResponse q =
                        selectQuestion(subjectCode, unit, bloomLevel,
                                m, usedQuestionIds, tempUsedIds);
                if (q != null) {
                    q.setOrderIndex(orderIndex);
                    q.setSubPart(optionLabel);
                    bestOption.add(q);
                    usedQuestionIds.addAll(tempUsedIds);
                    break;
                }
            }
        }

        return bestOption;
    }

    // Sum usage_count for candidate questions
    private long computeUsageSum(Set<Long> questionIds) {
        long sum = 0L;
        for (Long id : questionIds) {
            Question q = questionRepository.findById(id).orElse(null);
            if (q != null && q.getUsageCount() != null) {
                sum += q.getUsageCount();
            }
        }
        return sum;
    }

    private QuestionResponse selectQuestion(String subjectCode,
                                            Integer unit,
                                            Integer bloomLevel,
                                            Integer marks,
                                            Set<Long> usedQuestionIds,
                                            Set<Long> tempUsedIds) {

        log.info("Selecting question: subjectCode={}, unit={}, bloomLevel={}, marks={}", 
                 subjectCode, unit, bloomLevel, marks);

        // CONSTRAINT: If questions exist at requested bloom level, DO NOT fallback to lower levels
        // First, check if ANY question exists at the exact requested bloom level
        boolean existsAtRequestedLevel = 
            questionRepository.existsBySubjectCodeAndUnitAndExactBloomLevelAndMarksAndStatus(
                subjectCode, unit, bloomLevel, marks);

        log.info("Exists at requested bloom level {}: {}", bloomLevel, existsAtRequestedLevel);

        if (existsAtRequestedLevel) {
            // Must use only the requested bloom level
            List<Question> candidates =
                    questionRepository
                            .findBySubjectCodeAndUnitAndExactBloomLevelAndMarksAndStatus(
                                    subjectCode, unit, bloomLevel, marks);

            return selectFromCandidates(candidates, usedQuestionIds, tempUsedIds, bloomLevel, marks);
        } else {
            // Only fallback to lower levels if NO questions exist at requested level
            for (int b = bloomLevel - 1; b >= 1; b--) {
                
                List<Question> candidates =
                        questionRepository
                                .findBySubjectCodeAndUnitAndExactBloomLevelAndMarksAndStatus(
                                        subjectCode, unit, b, marks);

                if (!candidates.isEmpty()) {
                    log.info("No questions at bloom level {}, falling back to bloom level {}", bloomLevel, b);
                    return selectFromCandidates(candidates, usedQuestionIds, tempUsedIds, b, marks);
                }
            }
        }

        log.warn("No questions found for: subjectCode={}, unit={}, bloomLevel={}, marks={}", 
                 subjectCode, unit, bloomLevel, marks);
        return null;
    }

    private QuestionResponse selectFromCandidates(List<Question> candidates,
                                                  Set<Long> usedQuestionIds,
                                                  Set<Long> tempUsedIds,
                                                  Integer bloomLevel,
                                                  Integer marks) {

        // Filter by unused & ACTIVE
        List<Question> filtered = new ArrayList<>();
        for (Question q : candidates) {
            if (!usedQuestionIds.contains(q.getId())
                    && !tempUsedIds.contains(q.getId())
                    && "ACTIVE".equals(q.getStatus())) {
                filtered.add(q);
            }
        }

        if (filtered.isEmpty()) {
            log.debug("No unused questions available for bloom={}, marks={}", bloomLevel, marks);
            return null;
        }

        // Choose least usage_count among filtered
        filtered.sort(Comparator.comparingInt(q -> Optional.ofNullable(q.getUsageCount()).orElse(0)));

        Question chosen = filtered.get(0);
        log.info("Selected Question {} (Bloom Level {}): {} (Usage: {})", 
                 chosen.getId(), chosen.getBloomLevel(), chosen.getQuestionText(), chosen.getUsageCount());
        
        tempUsedIds.add(chosen.getId());
        return buildResponse(chosen, null, null);
    }

    // ==================== UTIL METHODS ====================

    private QuestionResponse buildResponse(Question q,
                                           Integer orderIndex,
                                           String subPart) {

        return QuestionResponse.builder()
                .questionId(q.getId())
                .questionText(q.getQuestionText())
                .unit(q.getUnit())
                .marks(q.getMarks())
                .bloomLevel(q.getBloomLevel())
                .orderIndex(orderIndex)
                .subPart(subPart)
                .build();
    }

    private List<Integer[]> getSplitCombinations(String examType) {

        if ("MODEL".equals(examType)) {
            return Arrays.asList(
                    new Integer[]{8, 8},
                    new Integer[]{10, 6},
                    new Integer[]{6, 10}
            );
        } else {
            return Arrays.asList(
                    new Integer[]{10, 5},
                    new Integer[]{5, 10},
                    new Integer[]{8, 7},
                    new Integer[]{7, 8}
            );
        }
    }

    private List<Integer> getFallbackMarks(String examType) {
        // For fallback when no 15/16 or splits are possible
        if ("MODEL".equals(examType)) {
            return Arrays.asList(16, 10, 8, 6);
        } else {
            return Arrays.asList(15, 10, 8, 7, 5);
        }
    }

    private List<Integer> getPartAUnits(String examType) {
        return switch (examType) {
            case "INTERNAL1" -> Arrays.asList(1, 2);
            case "INTERNAL2" -> Arrays.asList(3, 4);
            case "MODEL" -> Arrays.asList(1, 2, 3, 4, 5);
            default -> Arrays.asList(1, 2);
        };
    }

    private List<Integer> getPartBUnits(String examType) {
        return getPartAUnits(examType);
    }

    private int getQuestionsPerUnitPartA(String examType) {
        return "MODEL".equals(examType) ? 2 : 5;
    }

    private void updateQuestionUsage(List<Long> usedIds) {
        for (Long id : usedIds) {
            questionRepository.findById(id).ifPresent(q -> {
                q.setUsageCount(q.getUsageCount() + 1);
                questionRepository.save(q);
            });
        }
        log.info("Updated usage for {} questions", usedIds.size());
    }

    private void savePaperQuestions(Long paperId,
                                    Integer setNo,
                                    SetResponse setResponse) {
        log.info("Saving paper questions for paper: {}, set: {}", paperId, setNo);
        paperHistoryService.savePaperWithQuestions(paperId, setNo, setResponse.getPartA(), setResponse.getPartB());
    }
}
