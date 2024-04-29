package com.quizlier.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizlier.core.dto.QuestionRequest;
import com.quizlier.core.service.QuestionService;

public class QuestionController {
	private final QuestionService questionService;
	
	@Autowired
	public QuestionController(QuestionService questionService) {
		this.questionService = questionService;
	}
	
	@PostMapping(path = "{categoryId}")
	public ResponseEntity createQuestion(QuestionRequest request, @PathVariable("categoryId") Long categoryId) {
		return questionService.createQuestion(request, categoryId);
	}
	
	@GetMapping
	public ResponseEntity getAllQuestions() {
		return questionService.getAllQuestions();
	}
	
	@GetMapping(path = "{categoryId}")
	public ResponseEntity getAllQuestionsByCategory(@PathVariable("categoryId") Long categoryId) {
		return questionService.getAllQuestionsByCategory(categoryId);
	}
	
	@GetMapping(path = "{questionId}")
	public ResponseEntity getQuestion(@PathVariable("questionId") Long questionId) {
		return questionService.getQuestion(questionId);
	}
	
	@PutMapping(path = "{questionId}")
	public ResponseEntity updateQuestion(@PathVariable("questionId") Long questionId, @RequestParam(required = false) String questionText) {
		return questionService.updateQuestion(questionId, questionText);
	}
	
	@DeleteMapping(path = "{questionId}")
	public ResponseEntity deleteQuestion(@PathVariable("questionId") Long questionId) {
		return questionService.deleteQuestion(questionId);
	}
}
