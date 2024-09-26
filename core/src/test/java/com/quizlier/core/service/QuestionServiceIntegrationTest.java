package com.quizlier.core.service;

import com.quizlier.common.dto.QuestionRequest;
import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.entity.Question;
import com.quizlier.core.AbstractIntegrationTest;
import com.quizlier.core.TestUtil;
import com.quizlier.core.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QuestionServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void createQuestion_Success() {
        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        QuestionResponse questionResponse = questionService.createQuestion(questionRequest, 1L);

        assertNotNull(questionResponse);
        assertEquals(1, questionResponse.getCategoryId());

        Optional<Question> savedQuestion = questionRepository.findById(questionResponse.getId());

        assertTrue(savedQuestion.isPresent());
        assertEquals(questionRequest.getQuestion(), savedQuestion.get().getQuestion());

    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void getAllQuestions_Success() {
        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        QuestionResponse questionResponse = questionService.createQuestion(questionRequest, 1L);

        List<Question> questions = questionService.getAllQuestions();

        assertEquals(2, questions.size());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void getQuestion_Success() {
        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        QuestionResponse questionResponse = questionService.createQuestion(questionRequest, 1L);

        QuestionResponse question = questionService.getQuestion(questionResponse.getId());

        assertNotNull(question);
        assertEquals(question.getQuestion(), questionResponse.getQuestion());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void updateQuestion_Success() {
        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        QuestionResponse questionResponse = questionService.createQuestion(questionRequest, 1L);

        QuestionRequest updateQuestionRequest = new QuestionRequest();
        updateQuestionRequest.setQuestion("Test question");

        QuestionResponse updateQuestionResponse = questionService.updateQuestion(questionResponse.getId(), updateQuestionRequest);

        assertNotNull(updateQuestionResponse);

        Optional<Question> updatedQuestion = questionRepository.findById(questionResponse.getId());
        assertTrue(updatedQuestion.isPresent());
        assertEquals("Test question", updatedQuestion.get().getQuestion());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void deleteQuestion_Success() {
        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        QuestionResponse questionResponse = questionService.createQuestion(questionRequest, 1L);

        questionService.deleteQuestion(questionResponse.getId());

        Optional<Question> deletedQuestion = questionRepository.findById(questionResponse.getId());
        assertFalse(deletedQuestion.isPresent());

    }
}
