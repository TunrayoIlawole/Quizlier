package com.quizlier.core.service;

import com.quizlier.common.dto.QuestionRequest;
import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.entity.Category;
import com.quizlier.common.entity.Option;
import com.quizlier.common.entity.Question;
import com.quizlier.core.AbstractMockitoJUnitTest;
import com.quizlier.core.TestUtil;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.mappers.QuestionMapper;
import com.quizlier.core.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class QuestionServiceTest extends AbstractMockitoJUnitTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void createQuestion() {
        Long categoryId = 9L;
        Category category = TestUtil.buildCategory(categoryId);

        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        when(categoryService.getSingleCategory(categoryId)).thenReturn(category);
        when(questionRepository.getQuestion(questionRequest.getQuestion())).thenReturn(Optional.empty());

        Question question = TestUtil.buildQuestion(5L, categoryId);

        QuestionResponse questionResponse = TestUtil.buildQuestionResponse(9L, 5L);

        when(questionMapper.questionToQuestionresponse(any(Question.class))).thenReturn(questionResponse);

        var response = questionService.createQuestion(questionRequest, categoryId);

        assertNotNull(response);

    }

    @Test
    void createQuestion_invalidCategory() {
        Long categoryId = 10L;

        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        when(categoryService.getSingleCategory(categoryId)).thenReturn(null);

        var response = assertThrows(InvalidEntityException.class, () -> questionService.createQuestion(questionRequest, categoryId));

        assertEquals("Category with id '10' does not exist", response.getMessage());

    }

    @Test
    void createQuestion_duplicateQuestion() {
        Long categoryId = 11L;
        Category category = TestUtil.buildCategory(categoryId);

        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        Question existingQuestion = TestUtil.buildQuestion(10L, categoryId);

        when(categoryService.getSingleCategory(categoryId)).thenReturn(category);
        when(questionRepository.getQuestion(questionRequest.getQuestion())).thenReturn(Optional.of(existingQuestion));

        var response = assertThrows(DuplicateEntityException.class, () -> questionService.createQuestion(questionRequest, categoryId));

        assertEquals("Question already exists", response.getMessage());

    }

    @Test
    void getAllQuestions() {
        List<Question> questions = TestUtil.buildQuestions();

        when(questionRepository.findAll()).thenReturn(questions);

        var response = questionService.getAllQuestions();

        assertNotNull(response);
        assertNotEquals(0, response.size());
    }

    @Test
    void getQuestion() {
        Long questionId = 4L;
        Long categoryId = 9L;

        Question question = TestUtil.buildQuestion(4L, categoryId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        QuestionResponse questionResponse = TestUtil.buildQuestionResponse(questionId, categoryId);

        when(questionMapper.questionToQuestionresponse(any(Question.class))).thenReturn(questionResponse);

        var response = questionService.getQuestion(questionId);

        assertNotNull(response);
    }

    @Test
    void getQuestion_invalidQuestion() {
        Long questionId = 4L;

        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        var response = assertThrows(InvalidEntityException.class, () -> questionService.getQuestion(questionId));

        assertEquals("Question with id '4' does not exist", response.getMessage());
    }

    @Test
    void updateQuestion() {
        Long questionId = 4L;
        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        Question question = TestUtil.buildQuestion(questionId, 1L);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionRepository.getQuestion(questionRequest.getQuestion())).thenReturn(Optional.empty());

        QuestionResponse questionResponse = TestUtil.buildQuestionResponse(9L, questionId);

        when(questionMapper.questionToQuestionresponse(any(Question.class))).thenReturn(questionResponse);

        var response = questionService.updateQuestion(questionId, questionRequest);

        assertNotNull(response);
    }

    @Test
    void updateQuestion_invalidQuestion() {
        Long questionId = 3L;

        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        var response = assertThrows(InvalidEntityException.class, () -> questionService.deleteQuestion(questionId));
        assertEquals("Question with id '3' does not exist", response.getMessage());
    }

    @Test
    void updateQuestion_duplicateQuestion() {
        Long questionId = 4L;
        Long categoryId = 1L;
        QuestionRequest questionRequest = TestUtil.buildQuestionRequest();

        Question question = new Question();
        question.setCategory(TestUtil.buildCategory(categoryId));
        question.setQuestion("Random question");

        Question existingQuestion = new Question();
        question.setCategory(TestUtil.buildCategory(categoryId));
        question.setQuestion("Random question");

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionRepository.getQuestion(questionRequest.getQuestion())).thenReturn(Optional.of(existingQuestion));

        var response = assertThrows(DuplicateEntityException.class, () -> questionService.updateQuestion(questionId, questionRequest));

        assertEquals("Question text already exists", response.getMessage());
    }

//    @Test
    void deleteQuestion() {
        Long questionId = 4L;

        Question question = TestUtil.buildQuestion(questionId, 1L);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        // to be updated
    }

//    @Test
    void deleteQuestion_invalidQuestion() {
        Long questionId = 3L;

        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        var response = assertThrows(InvalidEntityException.class, () -> questionService.deleteQuestion(questionId));
        assertEquals("Question with id '4' does not exist", response.getMessage());
    }

    @Test
    void getQuestionsForCategory() {
        Long CategoryId = 6L;
        List<Question> questions = TestUtil.buildQuestionsForCategory(CategoryId);

        when(questionRepository.getQuestionsForCategory(CategoryId)).thenReturn(questions);

        var response = questionService.getQuestionsForCategory(CategoryId);

        assertNotNull(response);
        assertNotEquals(0, response.size());
    }
}