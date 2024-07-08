package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.dto.QuestionRequest;
import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.dto.QuestionResponseFull;
import com.quizlier.common.entity.Category;
import com.quizlier.common.entity.Option;
import com.quizlier.common.entity.Question;
import com.quizlier.core.repository.CategoryRepository;
import com.quizlier.core.repository.OptionRepository;
import com.quizlier.core.repository.QuestionRepository;

@Service
public class QuestionService {
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private OptionRepository optionRepository;
	
	public Question createQuestion(QuestionRequest request, Long categoryId) throws InvalidEntityException, DuplicateEntityException {
			Optional<Category> category = categoryRepository.findById(categoryId);
			
			if (category.isEmpty()) {
				throw new InvalidEntityException(String.format("Question with id %s does not exist", categoryId));
			}
			
			Optional<Question> existingQuestion = questionRepository.getQuestion(request.getQuestion());
			
			if (existingQuestion.isPresent()) {
				throw new DuplicateEntityException("Question already exists");
			}
			
			Question question = new Question();
			question.setQuestion(request.getQuestion());
			question.setCategory(category.get());
			question.setCreatedAt(Calendar.getInstance().getTime());
			
			questionRepository.save(question);
			
			return question;
	}
	
	public List<Question> getAllQuestions() {
			List<Question> questions = questionRepository.findAll();
			
			return questions;
	}
	
	public List<QuestionResponse> getAllQuestionsByCategory(Long categoryId) throws InvalidEntityException {
			Optional<Category> category = categoryRepository.findById(categoryId);
			
			if (category.isEmpty()) {
				throw new InvalidEntityException(String.format("Category with id %s does not exist", categoryId));
			}

			List<Question> questions = questionRepository.getQuestionsForCategory(categoryId);
			
		    List<QuestionResponse> responseList = new ArrayList<>();

		    questions.forEach(question -> {
		    	QuestionResponse questionResponse = new QuestionResponse();
		    	questionResponse.setId(question.getId());
		    	questionResponse.setQuestion(question.getQuestion());
		    	questionResponse.setCreatedAt(question.getCreatedAt());
		    	questionResponse.setUpdatedAt(question.getUpdatedAt());
		    	
		    	responseList.add(questionResponse);
		    });
			
			return responseList;
	}
	
	public QuestionResponseFull getQuestion(Long questionId) throws InvalidEntityException {
			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
			}
			
			QuestionResponseFull data = new QuestionResponseFull();
			
			data.setId(question.get().getId());
			data.setQuestion(question.get().getQuestion());
			data.setCreatedAt(question.get().getCreatedAt());
			data.setUpdatedAt(question.get().getUpdatedAt());
			
			List<OptionResponse> optionList = new ArrayList<>();
			
			List<Option> optionsByQuestion = optionRepository.getOptionsForQuestions(questionId);
			
			optionsByQuestion.forEach(option -> {
				OptionResponse optionResponse = new OptionResponse();
				optionResponse.setId(option.getId());
				optionResponse.setOptionText(option.getOption_text());
				optionResponse.setCorrect(option.getIsCorrect());
				optionResponse.setCreatedAt(option.getCreatedAt());
				optionResponse.setUpdatedAt(option.getUpdatedAt());
				
				optionList.add(optionResponse);
			});
			
			data.setOptions(optionList);
			
			return data;
	}
	
	public Question updateQuestion(Long questionId, String questionText) throws InvalidEntityException, DuplicateEntityException {
			Optional<Question> question = questionRepository.findById(questionId);
		
			if (question.isEmpty()) {
				throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
			}
			
			if (questionText != null && questionText.length() > 0 && !Objects.equals(question.get().getQuestion(), questionText)) {
				Optional<Question> existingQuestion = questionRepository.getQuestion(questionText);
				if (existingQuestion.isPresent()) {
					throw new DuplicateEntityException(String.format("Question text %s already taken", questionText));
				}
				question.get().setQuestion(questionText);
			}

			question.get().setUpdatedAt(Calendar.getInstance().getTime());
			questionRepository.save(question.get());
			
			return question.get();
	}
	
	public void deleteQuestion(Long questionId) throws InvalidEntityException {
		boolean exists = questionRepository.existsById(questionId);
		
		if (!exists) {
			throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
		}
		questionRepository.deleteById(questionId);

	}

}
