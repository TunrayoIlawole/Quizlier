package com.quizlier.core.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;

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
import com.quizlier.common.entity.Question;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.service.QuestionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/question")
public class QuestionController {
	private final QuestionService questionService;
	
	@PostMapping(path = "{categoryId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity createQuestion(@RequestBody QuestionRequest request, @PathVariable Long categoryId) {
		QuestionResponse question = questionService.createQuestion(request, categoryId);

		ResponseData<QuestionResponse> response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(question);

		return ResponseEntity.ok(response);

	}
	
	@GetMapping
	public ResponseEntity getAllQuestions() {
		List<Question> questions = questionService.getAllQuestions();

		ResponseData<List<Question>> response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(questions);

		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping(path = "{questionId}")
	public ResponseEntity getQuestion(@PathVariable Long questionId) {

		QuestionResponse question = questionService.getQuestion(questionId);

		ResponseData<QuestionResponse> response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(question);

		return ResponseEntity.ok().body(response);

	}
	
	@PutMapping(path = "{questionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequest questionRequest) {
		QuestionResponse question = questionService.updateQuestion(questionId, questionRequest);

		ResponseData<QuestionResponse> response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(question);
		return ResponseEntity.ok().body(response);

	}
	
	@DeleteMapping(path = "{questionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity deleteQuestion(@PathVariable Long questionId) {
		questionService.deleteQuestion(questionId);

		ResponseData response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);

		return ResponseEntity.ok().body(response);

	}
}
