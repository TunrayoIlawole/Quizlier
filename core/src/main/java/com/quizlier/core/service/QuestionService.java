package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.core.mappers.OptionMapper;
import com.quizlier.core.mappers.QuestionMapper;
import org.springframework.stereotype.Service;

import com.quizlier.common.dto.QuestionRequest;
import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.entity.Category;
import com.quizlier.common.entity.Question;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;

import com.quizlier.core.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;

	private final CategoryService categoryService;

	private final QuestionMapper questionMapper;


	public QuestionResponse createQuestion(QuestionRequest request, Long categoryId) throws InvalidEntityException, DuplicateEntityException {
			Category category = categoryService.getSingleCategory(categoryId);
			
			if (category == null) {
				throw new InvalidEntityException(ServiceMessages.invalidEntity("Category", categoryId.toString()));
			}
			
			Optional<Question> existingQuestion = questionRepository.getQuestion(request.getQuestion());
			
			if (existingQuestion.isPresent()) {
				throw new DuplicateEntityException(ServiceMessages.duplicateEntity("Question"));
			}
			
			Question question = new Question();
			question.setQuestion(request.getQuestion());
			question.setCategory(category);
			question.setCreatedAt(Calendar.getInstance().getTime());
			questionRepository.save(question);

			QuestionResponse questionResponse = questionMapper.questionToQuestionresponse(question);
			questionResponse.setCategoryId(categoryId);
			
			return questionResponse;
	}
	
	public List<Question> getAllQuestions() {
			List<Question> questions = questionRepository.findAll();
			
			return questions;
	}
	
//	public List<QuestionResponse> getAllQuestionsByCategory(Long categoryId) throws InvalidEntityException {
//		Category category = categoryService.getSingleCategory(categoryId);
//
//		if (category == null) {
//			throw new InvalidEntityException(ServiceMessages.invalidEntity("Category", categoryId.toString()));
//		}
//
//		List<Question> questions = questionRepository.getQuestionsForCategory(categoryId);
//
//		List<QuestionResponse> responseList = new ArrayList<>();
//
//		questions.forEach(question -> {
//			QuestionResponse questionResponse = questionMapper.questionToQuestionresponse(question);
//			questionResponse.setCategoryId(categoryId);
//
//			responseList.add(questionResponse);
//		});
//
//		return responseList;
//	}
	
	public QuestionResponse getQuestion(Long questionId) throws InvalidEntityException {
			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				throw new InvalidEntityException(ServiceMessages.invalidEntity("Question", questionId.toString()));
			}
			
			QuestionResponse data = new QuestionResponse();
			
			data.setId(question.get().getId());
			data.setQuestion(question.get().getQuestion());
			
			return data;
	}
	
	public QuestionResponse updateQuestion(Long questionId, QuestionRequest questionRequest) throws InvalidEntityException, DuplicateEntityException {
			Optional<Question> question = questionRepository.findById(questionId);
		
			if (question.isEmpty()) {
				throw new InvalidEntityException(ServiceMessages.invalidEntity("Question", questionId.toString()));
			}
			
			if (questionRequest.getQuestion() != null && !Objects.equals(question.get().getQuestion(), questionRequest.getQuestion())) {
				Optional<Question> existingQuestion = questionRepository.getQuestion(questionRequest.getQuestion());
				if (existingQuestion.isPresent()) {
					throw new DuplicateEntityException(ServiceMessages.duplicateEntity("Question text"));
				}
				question.get().setQuestion(questionRequest.getQuestion());
			}

			question.get().setUpdatedAt(Calendar.getInstance().getTime());
			questionRepository.save(question.get());

			QuestionResponse questionResponse = questionMapper.questionToQuestionresponse(question.get());
			questionResponse.setCategoryId(question.get().getCategory().getId());
			
			return questionResponse;
	}
	
	public void deleteQuestion(Long questionId) throws InvalidEntityException {
		boolean exists = questionRepository.existsById(questionId);
		
		if (!exists) {
			throw new InvalidEntityException(ServiceMessages.invalidEntity("Question", questionId.toString()));
		}
		questionRepository.deleteById(questionId);

	}

	public List<Question> getQuestionsForCategory(Long categoryId) {
		List<Question> questionsByCategory = questionRepository.getQuestionsForCategory(categoryId);

		return questionsByCategory;
	}

	public Question getSingleQuestion(Long id) {
		Optional<Question> question = questionRepository.findById(id);

		return question.orElse(null);
	}

}
