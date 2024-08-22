package com.quizlier.core.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quizlier.common.dto.QuestionRequest;
import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.dto.QuestionResponseFull;
import com.quizlier.common.entity.Question;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.service.QuestionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/question")
public class QuestionController {
	private final QuestionService questionService;
	
	@PostMapping(path = "{categoryId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity createQuestion(@RequestBody QuestionRequest request, @PathVariable Long categoryId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			QuestionResponse question = questionService.createQuestion(request, categoryId);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(question);
			return ResponseEntity.ok(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		} catch (DuplicateEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@GetMapping
	public ResponseEntity getAllQuestions() {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			List<Question> questions = questionService.getAllQuestions();
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(questions);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

//	@GetMapping(path = "{categoryId}")
//	public ResponseEntity getAllQuestionsByCategory(@PathVariable("categoryId") Long categoryId) {
//		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
//
//		try {
//			List<QuestionResponse> questions = questionService.getAllQuestionsByCategory(categoryId);
//			response.setStatus(ServiceStatusCodes.SUCCESS);
//			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
//			response.setData(questions);
//			return ResponseEntity.ok().body(response);
//		} catch (InvalidEntityException e) {
//			response.setMessage(e.getMessage());
//			return ResponseEntity.notFound().build();
//		} catch (Exception e) {
//			return ResponseEntity.internalServerError().body(e.getMessage());
//		}
//	}
	
	@GetMapping(path = "{questionId}")
	public ResponseEntity getQuestion(@PathVariable("questionId") Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			QuestionResponseFull question = questionService.getQuestion(questionId);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(question);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PutMapping(path = "{questionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity updateQuestion(@PathVariable("questionId") Long questionId, @RequestBody QuestionRequest questionRequest) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			QuestionResponse question = questionService.updateQuestion(questionId, questionRequest);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(question);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		} catch (DuplicateEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}  catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@DeleteMapping(path = "{questionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity deleteQuestion(@PathVariable("questionId") Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			questionService.deleteQuestion(questionId);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}  catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}
