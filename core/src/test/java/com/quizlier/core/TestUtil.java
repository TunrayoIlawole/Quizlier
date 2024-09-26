package com.quizlier.core;

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.dto.QuestionRequest;
import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.entity.Category;
import com.quizlier.common.entity.Option;
import com.quizlier.common.entity.Question;
import com.quizlier.core.service.QuestionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestUtil {

    public static Category buildCategory(Long categoryId) {
        Category category = new Category();

        category.setId(categoryId);
        category.setName(RandomStringUtils.randomAlphabetic(9));
        category.setDescription(RandomStringUtils.randomAlphanumeric(56));
        category.setCreatedAt(new Date());

        return category;
    }

    public static List<Category> buildCategories() {
        List<Category> categories = new ArrayList<>();

        Category category = buildCategory(4L);
        Category category1 = buildCategory(8L);

        categories.add(category);
        categories.add(category1);

        return categories;
    }

    public static QuestionRequest buildQuestionRequest() {
        QuestionRequest questionRequest = new QuestionRequest();

        questionRequest.setQuestion(RandomStringUtils.randomAlphabetic(45));

        return questionRequest;
    }

    public static QuestionResponse buildQuestionResponse(Long categoryId, Long questionId) {
        QuestionResponse questionResponse = new QuestionResponse();

        questionResponse.setCategoryId(categoryId);
        questionResponse.setId(questionId);
        questionResponse.setQuestion(RandomStringUtils.randomAlphabetic(45));

        return questionResponse;
    }

    public static Question buildQuestion(Long questionId, Long categoryId) {
        Question question = new Question();

        question.setId(questionId);
        question.setQuestion(RandomStringUtils.randomAlphabetic(45));
        question.setCategory(buildCategory(categoryId));
        question.setCreatedAt(new Date());

        return question;
    }

    public static List<Question> buildQuestions() {
        List<Question> questions = new ArrayList<>();

        Question question1 = buildQuestion(2L, 7L);
        Question question2 = buildQuestion(5L, 9L);

        questions.add(question1);
        questions.add(question2);

        return questions;
    }

    public static List<Question> buildQuestionsForCategory(Long categoryid) {
        List<Question> questions = new ArrayList<>();

        Question question1 = buildQuestion(3L, categoryid);
        Question question2 = buildQuestion(8L, categoryid);

        questions.add(question1);
        questions.add(question2);

        return questions;
    }

    public static Option buildOption(Long optionId, Boolean isCorrect) {
        Option option = new Option();

        option.setId(optionId);
        option.setIsCorrect(isCorrect);
        option.setOptionText(RandomStringUtils.randomAlphabetic(12));
        option.setQuestion(buildQuestion(7L, 2L));
        option.setCreatedAt(new Date());

        return option;
    }

    public static Option buildOption(Long questionId, Long optionId, Boolean isCorrect) {
        Option option = new Option();

        option.setId(optionId);
        option.setIsCorrect(isCorrect);
        option.setOptionText(RandomStringUtils.randomAlphabetic(12));
        option.setQuestion(buildQuestion(questionId, 2L));
        option.setCreatedAt(new Date());

        return option;
    }

    public static List<Option> buildOptions() {
        List<Option> options = new ArrayList<>();

        options.add(buildOption(1L, true));
        options.add(buildOption(2L, false));
        options.add(buildOption(3L, false));
//        options.add(buildOption(4L, false));

        return options;
    }

    public static List<Option> buildFullOptions() {
        List<Option> options = new ArrayList<>();

        options.add(buildOption(1L, true));
        options.add(buildOption(2L, false));
        options.add(buildOption(3L, false));
        options.add(buildOption(4L, false));

        return options;
    }

    public static List<Option> buildOptionsForQuestion(Long questionId) {
        List<Option> options = new ArrayList<>();

        options.add(buildOption(questionId, 1L, true));
        options.add(buildOption(questionId, 2L, false));
        options.add(buildOption(questionId, 3L, false));
        options.add(buildOption(questionId, 4L, false));

        return options;
    }

    public static List<Option> buildInvalidOptions() {
        List<Option> options = new ArrayList<>();

        options.add(buildOption(1L, false));
        options.add(buildOption(2L, false));
        options.add(buildOption(3L, false));
        options.add(buildOption(4L, false));

        return options;
    }

    public static OptionRequest buildOptionRequest() {
        OptionRequest optionRequest = new OptionRequest();

        optionRequest.setOptionText(RandomStringUtils.randomAlphabetic(12));
        optionRequest.setIsCorrect(false);

        return optionRequest;
    }

    public static OptionRequest buildCorrectOptionRequest() {
        OptionRequest optionRequest = new OptionRequest();

        optionRequest.setOptionText(RandomStringUtils.randomAlphabetic(12));
        optionRequest.setIsCorrect(true);

        return optionRequest;
    }

    public static OptionResponse buildOptionResponse(Long optionId, Long questionId) {
        OptionResponse optionResponse = new OptionResponse();

        optionResponse.setId(optionId);
        optionResponse.setOptionText(RandomStringUtils.randomAlphabetic(12));
        optionResponse.setIsCorrect(false);
        optionResponse.setQuestionId(questionId);

        return optionResponse;
    }


}
