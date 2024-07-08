package com.quizlier.core.controllers;

import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.dto.QuestionResponseFull;
import com.quizlier.common.entity.Question;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizlier.common.dto.QuestionRequest;
import com.quizlier.core.service.QuestionService;

import java.util.List;

public class QuestionController {
	private final QuestionService questionService;
	
	@Autowired
	public QuestionController(QuestionService questionService) {
		this.questionService = questionService;
	}
	
	@PostMapping(path = "{categoryId}")
	public ResponseEntity createQuestion(QuestionRequest request, @PathVariable("categoryId") Long categoryId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			Question question = questionService.createQuestion(request, categoryId);
			response.setData(question);
			return ResponseEntity.ok().body(response);
		} catch (DuplicateEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@GetMapping
	public ResponseEntity getAllQuestions() {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			List<Question> questions = questionService.getAllQuestions();
			response.setData(questions);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

	@GetMapping(path = "{categoryId}")
	public ResponseEntity getAllQuestionsByCategory(@PathVariable("categoryId") Long categoryId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			List<QuestionResponse> questions = questionService.getAllQuestionsByCategory(categoryId);
			response.setData(questions);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@GetMapping(path = "{questionId}")
	public ResponseEntity getQuestion(@PathVariable("questionId") Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			QuestionResponseFull question = questionService.getQuestion(questionId);
			response.setData(question);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PutMapping(path = "{questionId}")
	public ResponseEntity updateQuestion(@PathVariable("questionId") Long questionId, @RequestParam(required = false) String questionText) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			Question question = questionService.updateQuestion(questionId, questionText);
			response.setData(question);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (DuplicateEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.badRequest().build();
		}  catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@DeleteMapping(path = "{questionId}")
	public ResponseEntity deleteQuestion(@PathVariable("questionId") Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			questionService.deleteQuestion(questionId);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		}  catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}
