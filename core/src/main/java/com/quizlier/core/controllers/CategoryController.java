package com.quizlier.core.controllers;

import com.quizlier.common.dto.CategoryResponse;
import com.quizlier.common.dto.CategoryResponseFull;
import com.quizlier.common.entity.Category;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.core.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
	
	private final CategoryService categoryService;
	
	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@PostMapping
	public ResponseEntity createCategory(CategoryRequest request) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
		try {
			Category category = categoryService.createCategory(request);
			response.setData(category);

			return ResponseEntity.ok().body(response);
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
			response.setData(category);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		}
		catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@DeleteMapping(path = "{categoryId}")
	public ResponseEntity deleteCategory(@PathVariable("categoryId") Long categoryId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			categoryService.deleteCategory(categoryId);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PutMapping(path = "{categoryId}")
	public ResponseEntity updateCategory(@PathVariable("categoryId") Long categoryId, @RequestParam(required = false) String name, @RequestParam(required = false) String description) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			Category category = categoryService.updateCategory(categoryId, name, description);
			response.setData(category);
			return ResponseEntity.ok().body(response);
		} catch (InvalidEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (DuplicateEntityException e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	

}
