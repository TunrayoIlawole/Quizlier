package com.quizlier.core.controllers;

import com.quizlier.common.dto.CategoryResponse;
import com.quizlier.common.dto.CategoryResponseFull;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.core.service.CategoryService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/category")
public class CategoryController {
	
	private final CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity createCategory(@RequestBody CategoryRequest request) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
		try {
			CategoryResponse category = categoryService.createCategory(request);
			response.setData(category);

			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			return ResponseEntity.ok(response);
		} catch (DuplicateEntityException ex) {
			response.setMessage(ex.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@GetMapping
	public ResponseEntity getAllCategories() {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			List<CategoryResponse> categories = categoryService.getAllCategories();
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(categories);
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	@GetMapping(path = "{categoryId}")
	public ResponseEntity getCategory(@PathVariable("categoryId") Long categoryId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			CategoryResponseFull category = categoryService.getCategory(categoryId);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(category);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}
		catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@DeleteMapping(path = "{categoryId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity deleteCategory(@PathVariable("categoryId") Long categoryId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			categoryService.deleteCategory(categoryId);
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
	
	@PutMapping(path = "{categoryId}")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public ResponseEntity updateCategory(@PathVariable("categoryId") Long categoryId, @RequestBody CategoryRequest categoryRequest) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			CategoryResponse category = categoryService.updateCategory(categoryId, categoryRequest);
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(category);
			return ResponseEntity.ok().body(response);
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
	
	

}
