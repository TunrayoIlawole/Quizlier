package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.dto.OptionResponse;
import com.quizlier.core.dto.QuestionRequest;
import com.quizlier.core.dto.QuestionResponse;
import com.quizlier.core.dto.QuestionResponseFull;
import com.quizlier.core.entity.Category;
import com.quizlier.core.entity.Option;
import com.quizlier.core.entity.Question;
import com.quizlier.core.repository.CategoryRepository;
import com.quizlier.core.repository.OptionRepository;
import com.quizlier.core.repository.QuestionRepository;

public class QuestionService {
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	OptionRepository optionRepository;
	
	public ResponseEntity createQuestion(QuestionRequest request, Long categoryId) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
			
			Optional<Category> category = categoryRepository.findById(categoryId);
			
			if (category.isEmpty()) {
				response.setMessage(null);
				return ResponseEntity.badRequest().body(response);
			}
			
			Optional<Question> existingQuestion = questionRepository.getQuestion(request.getQuestion());
			
			if (existingQuestion.isPresent()) {
				response.setMessage(null);
				return ResponseEntity.badRequest().body(response);
			}
			
			Question question = new Question();
			question.setQuestion(request.getQuestion());
			question.setCategory(category.get());
			question.setCreatedAt(Calendar.getInstance().getTime());
			
			questionRepository.save(question);
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(question);
			
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	public ResponseEntity getAllQuestions() {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

			List<Question> questions = questionRepository.findAll();
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setData(questions);
			response.setMessage(null);
			
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			// TODO: handle exception
			
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	public ResponseEntity getAllQuestionsByCategory(Long categoryId) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
			
			Optional<Category> category = categoryRepository.findById(categoryId);
			
			if (category.isEmpty()) {
				response.setMessage(null);
				return ResponseEntity.badRequest().body(response);
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
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setData(responseList);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			// TODO: handle exception
			
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	public ResponseEntity getQuestion(Long questionId) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				response.setMessage(String.format("Question with id %s does not exist", questionId));
				return ResponseEntity.notFound().build();
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
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(data);
			
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			// TODO: handle exception
			
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	public ResponseEntity updateQuestion(Long questionId, String questionText) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

			Optional<Question> question = questionRepository.findById(questionId);
		
			if (question.isEmpty()) {
				response.setMessage(String.format("Question with id %s does not exist", questionId));
				return ResponseEntity.notFound().build();
			}
			
			
			if (questionText != null && questionText.length() > 0 && !Objects.equals(question.get().getQuestion(), questionText)) {
				Optional<Question> existingQuestion = questionRepository.getQuestion(questionText);
				if (existingQuestion.isPresent()) {
					response.setMessage(String.format("Question text %s already taken", questionText));
					return ResponseEntity.notFound().build();
				}
				question.get().setQuestion(questionText);
			}

			question.get().setUpdatedAt(Calendar.getInstance().getTime());
			questionRepository.save(question.get());
			
			return ResponseEntity.ok().body(response);

			
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
		
		
		
	}
	
	public ResponseEntity deleteQuestion(Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		boolean exists = questionRepository.existsById(questionId);
		
		if (!exists) {
			response.setMessage(String.format("Question with id %s does not exist", questionId));
			return ResponseEntity.notFound().build();
		}
		questionRepository.deleteById(questionId);
		
		return ResponseEntity.ok().body(response);

	}

}
