package com.quizlier.core.controllers;

import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.core.service.OptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/option")
public class OptionController {
	private final OptionService optionService;
	
	@PostMapping(path = "{questionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity createOption(@RequestBody OptionRequest request, @PathVariable Long questionId) {

		OptionResponse option = optionService.createOption(request, questionId);

		ResponseData<OptionResponse> response = new ResponseData<OptionResponse>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);

		response.setData(option);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "{questionId}")
	public ResponseEntity getAllOptions(@PathVariable Long questionId) {
		List<OptionResponse> options = optionService.getAllOptionsByQuestions(questionId);

		ResponseData<List<OptionResponse>> response = new ResponseData<List<OptionResponse>>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(options);

		return ResponseEntity.ok().body(response);
	}
	
	@DeleteMapping(path = "{optionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity deleteOption(@PathVariable Long optionId) {
		optionService.deleteOption(optionId);

		ResponseData response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);

		return ResponseEntity.ok().body(response);
	}
	
	@PutMapping(path = "{optionId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity updateOption(@PathVariable Long optionId, @RequestBody OptionRequest optionRequest) {
		OptionResponse option = optionService.updateOption(optionId, optionRequest);

		ResponseData<OptionResponse> response = new ResponseData<OptionResponse>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(option);

		return ResponseEntity.ok().body(response);

	}
}
