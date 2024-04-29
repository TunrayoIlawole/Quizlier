package com.quizlier.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.core.service.OptionService;

public class OptionController {
	private final OptionService optionService;
	
	@Autowired
	public OptionController(OptionService optionService) {
		this.optionService = optionService;
	}
	
	@PostMapping(path = "{questionId}")
	public ResponseEntity createOption(OptionRequest request, @PathVariable("questionId") Long questionId) {
		return optionService.createOption(request, questionId);
	}
	
	@GetMapping(path = "{questionId}")
	public ResponseEntity getAllOptions(@PathVariable("questionId") Long questionId) {
		return optionService.getAllOptionsByQuestions(questionId);
	}
	
	@DeleteMapping(path = "{optionId}")
	public ResponseEntity deleteCategory(@PathVariable("optionId") Long optionId) {
		return optionService.deleteOption(optionId);
	}
	
	@PutMapping(path = "{optionId}")
	public ResponseEntity updateOption(@PathVariable("optionId") Long optionId, @RequestParam(required = false) String optionText, @RequestParam(required = false) Boolean isCorrect) {
		return optionService.updateOption(optionId, optionText, isCorrect);
	}
}
