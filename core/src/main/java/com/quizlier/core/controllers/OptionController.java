package com.quizlier.core.controllers;

import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.entity.Option;
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

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.core.service.OptionService;

import java.util.List;

public class OptionController {
	private final OptionService optionService;
	
	@Autowired
	public OptionController(OptionService optionService) {
		this.optionService = optionService;
	}
	
	@PostMapping(path = "{questionId}")
	public ResponseEntity createOption(OptionRequest request, @PathVariable("questionId") Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			Option option = optionService.createOption(request, questionId);
			response.setData(option);
			return ResponseEntity.ok().body(response);
		} catch (DuplicateEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@GetMapping(path = "{questionId}")
	public ResponseEntity getAllOptions(@PathVariable("questionId") Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			List<OptionResponse> options = optionService.getAllOptionsByQuestions(questionId);

			response.setData(options);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@DeleteMapping(path = "{optionId}")
	public ResponseEntity deleteCategory(@PathVariable("optionId") Long optionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			optionService.deleteOption(optionId);

			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PutMapping(path = "{optionId}")
	public ResponseEntity updateOption(@PathVariable("optionId") Long optionId, @RequestParam(required = false) String optionText, @RequestParam(required = false) Boolean isCorrect) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			Option option = optionService.updateOption(optionId, optionText, isCorrect);

			response.setData(option);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}
