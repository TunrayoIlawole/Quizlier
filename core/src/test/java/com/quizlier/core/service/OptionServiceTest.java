package com.quizlier.core.service;

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.entity.Option;
import com.quizlier.common.entity.Question;
import com.quizlier.core.AbstractMockitoJUnitTest;
import com.quizlier.core.TestUtil;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.exceptions.MaximumEntityException;
import com.quizlier.core.mappers.OptionMapper;
import com.quizlier.core.repository.OptionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OptionServiceTest extends AbstractMockitoJUnitTest {

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private QuestionService questionService;

    @Mock
    private OptionMapper optionMapper;

    @InjectMocks
    private OptionService optionService;

    @Test
    void createOption() {
        Long questionId = 4L;
        OptionRequest optionRequest = TestUtil.buildOptionRequest();

        Question question = TestUtil.buildQuestion(questionId, 2L);

        when(questionService.getSingleQuestion(questionId)).thenReturn(question);
        when(optionRepository.getOptionsForQuestions(questionId)).thenReturn(TestUtil.buildOptions());

        Option option = TestUtil.buildOption(4L, false);

        OptionResponse optionResponse = TestUtil.buildOptionResponse(4L, questionId);

        when(optionMapper.optionToOptionresponse(any(Option.class))).thenReturn(optionResponse);

        var response = optionService.createOption(optionRequest, questionId);

        assertNotNull(response);
    }

    @Test
    void createOption_maximumOptions() {
        Long questionId = 4L;
        OptionRequest optionRequest = TestUtil.buildOptionRequest();

        Question question = TestUtil.buildQuestion(questionId, 2L);

        when(questionService.getSingleQuestion(questionId)).thenReturn(question);
        when(optionRepository.getOptionsForQuestions(questionId)).thenReturn(TestUtil.buildFullOptions());

        var response = assertThrows(MaximumEntityException.class, () -> optionService.createOption(optionRequest, questionId));

        assertEquals("Maximum number of options for question exceeded", response.getMessage());
    }

    @Test
    void getAllOptionsByQuestions() {
        Long questionId = 4L;

        Question question = TestUtil.buildQuestion(questionId, 2L);
        List<Option> options = TestUtil.buildOptions();
        List<OptionResponse> optionResponses = new ArrayList<>();

        when(questionService.getSingleQuestion(questionId)).thenReturn(question);
        when(optionRepository.getOptionsForQuestions(questionId)).thenReturn(options);

        var response = optionService.getAllOptionsByQuestions(questionId);

        assertNotNull(response);
    }

    @Test
    void getAllOptionsByQuestions_NoAnswer() {
        Long questionId = 4L;

        Question question = TestUtil.buildQuestion(questionId, 2L);

        when(questionService.getSingleQuestion(questionId)).thenReturn(question);
        when(optionRepository.getOptionsForQuestions(questionId)).thenReturn(TestUtil.buildInvalidOptions());

        var response = assertThrows(InvalidEntityException.class, () ->optionService.getAllOptionsByQuestions(questionId));

        assertEquals("Question does not have a correct answer set", response.getMessage());
    }

    @Test
    void updateOption() {
        Long optionId = 3L;

        OptionRequest optionRequest = TestUtil.buildOptionRequest();
        Option option = TestUtil.buildOption(optionId, false);

        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        OptionResponse optionResponse = TestUtil.buildOptionResponse(optionId, 10L);

        when(optionMapper.optionToOptionresponse(any(Option.class))).thenReturn(optionResponse);

        var response = optionService.updateOption(optionId, optionRequest);

        assertNotNull(response);
    }

    @Test
    void updateOption_correctAnswer() {
        Long optionId = 3L;

        OptionRequest optionRequest = TestUtil.buildCorrectOptionRequest();
        Option option = TestUtil.buildOption(optionId, false);

        Question question = option.getQuestion();

        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(optionRepository.getOptionsForQuestions(question.getId())).thenReturn(TestUtil.buildOptions());

        OptionResponse optionResponse = TestUtil.buildOptionResponse(optionId, 10L);

        when(optionMapper.optionToOptionresponse(any(Option.class))).thenReturn(optionResponse);

        var response = optionService.updateOption(optionId, optionRequest);

        assertNotNull(response);
        assertEquals(true, option.getIsCorrect());
    }

//    @Test
    void deleteOption() {
        Long optionId = 5L;

        when(optionRepository.existsById(optionId)).thenReturn(true);

        optionService.deleteOption(optionId);

    }
}