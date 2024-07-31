package com.quizlier.core.controllers;

import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.entity.Option;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.exceptions.MaximumEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.core.service.OptionService;

import java.util.List;

@RestController
@RequestMapping("api/v1/option")
public class OptionController {
	private final OptionService optionService;
	
	@Autowired
	public OptionController(OptionService optionService) {
		this.optionService = optionService;
	}
	
	@PostMapping(path = "{questionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity createOption(@RequestBody OptionRequest request, @PathVariable("questionId") Long questionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			OptionResponse option = optionService.createOption(request, questionId);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(option);
			return ResponseEntity.ok().body(response);
		} catch (DuplicateEntityException | MaximumEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
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
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@DeleteMapping(path = "{optionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity deleteOption(@PathVariable("optionId") Long optionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			optionService.deleteOption(optionId);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PutMapping(path = "{optionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity updateOption(@PathVariable("optionId") Long optionId, @RequestBody OptionRequest optionRequest) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			OptionResponse option = optionService.updateOption(optionId, optionRequest);

			response.setData(option);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}
