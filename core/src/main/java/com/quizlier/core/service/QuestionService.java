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
	
	public QuestionResponse createQuestion(QuestionRequest request, Long categoryId) throws InvalidEntityException, DuplicateEntityException {
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

			QuestionResponse questionResponse = new QuestionResponse();
			questionResponse.setId(question.getId());
			questionResponse.setQuestion(request.getQuestion());
			questionResponse.setCategoryId(categoryId);
			
			return questionResponse;
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
			
			List<OptionResponse> optionList = new ArrayList<>();
			
			List<Option> optionsByQuestion = optionRepository.getOptionsForQuestions(questionId);
			
			optionsByQuestion.forEach(option -> {
				OptionResponse optionResponse = new OptionResponse();
				optionResponse.setId(option.getId());
				optionResponse.setOptionText(option.getOption_text());
				optionResponse.setIsCorrect(option.getIsCorrect());
				optionResponse.setQuestionId(questionId);
				
				optionList.add(optionResponse);
			});
			
			data.setOptions(optionList);
			
			return data;
	}
	
	public QuestionResponse updateQuestion(Long questionId, QuestionRequest questionRequest) throws InvalidEntityException, DuplicateEntityException {
			Optional<Question> question = questionRepository.findById(questionId);
		
			if (question.isEmpty()) {
				throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
			}
			
			if (questionRequest.getQuestion() != null && !Objects.equals(question.get().getQuestion(), questionRequest.getQuestion())) {
				Optional<Question> existingQuestion = questionRepository.getQuestion(questionRequest.getQuestion());
				if (existingQuestion.isPresent()) {
					throw new DuplicateEntityException(String.format("Question text %s already taken", questionRequest.getQuestion()));
				}
				question.get().setQuestion(questionRequest.getQuestion());
			}

			question.get().setUpdatedAt(Calendar.getInstance().getTime());
			questionRepository.save(question.get());

			QuestionResponse questionResponse = new QuestionResponse();
			questionResponse.setId(questionId);
			questionResponse.setQuestion(questionRequest.getQuestion());
			questionResponse.setCategoryId(question.get().getCategory().getId());
			
			return questionResponse;
	}
	
	public void deleteQuestion(Long questionId) throws InvalidEntityException {
		boolean exists = questionRepository.existsById(questionId);
		
		if (!exists) {
			throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
		}
		questionRepository.deleteById(questionId);

	}

}
