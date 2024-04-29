package com.quizlier.core.controllers;

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
		return categoryService.createCategory(request);
	}
	
	@GetMapping
	public ResponseEntity getAllCategories() {
		return categoryService.getAllCategories();
	}
	
	@GetMapping(path = "{categoryId}")
	public ResponseEntity getCategory(@PathVariable("categoryId") Long categoryId) {
		return categoryService.getCategory(categoryId);
	}
	
	@DeleteMapping(path = "{categoryId}")
	public ResponseEntity deleteCategory(@PathVariable("categoryId") Long categoryId) {
		return categoryService.deleteCategory(categoryId);
	}
	
	@PutMapping(path = "{categoryId}")
	public ResponseEntity updateCategory(@PathVariable("categoryId") Long categoryId, @RequestParam(required = false) String name, @RequestParam(required = false) String description) {
		return categoryService.updateCategory(categoryId, name, description);
	}
	
	

}
