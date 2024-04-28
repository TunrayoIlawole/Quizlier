package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.dto.CategoryRequest;
import com.quizlier.core.dto.CategoryResponse;
import com.quizlier.core.dto.CategoryResponseFull;
import com.quizlier.core.dto.QuestionResponse;
import com.quizlier.core.entity.Category;
import com.quizlier.core.entity.Question;
import com.quizlier.core.repository.CategoryRepository;
import com.quizlier.core.repository.QuestionRepository;

public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	
	private ResponseEntity createCategory(CategoryRequest request) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
			
			Optional<Category> categoryByName = categoryRepository.findCategoryByName(request.getName());
			
			if (categoryByName.isPresent()) {
				response.setMessage(null);
				return ResponseEntity.badRequest().body(response);
			}
			
			Category category = new Category();
			category.setName(request.getName());
			category.setDescription(request.getDescription());
			category.setCreatedAt(Calendar.getInstance().getTime());
			
			categoryRepository.save(category);
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(category);
			
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	private ResponseEntity getAllCategories() {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

			List<Category> categories = categoryRepository.findAll();
			
			List<CategoryResponse> responseList = new ArrayList<>();
			
			categories.forEach(category -> {
				CategoryResponse data = new CategoryResponse();
				data.setId(category.getId());
				data.setName(category.getName());
				data.setDescription(category.getDescription());
				data.setCreatedAt(category.getCreatedAt());
				data.setUpdatedAt(category.getUpdatedAt());
				
				responseList.add(data);
			});
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(responseList);
			
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			// TODO: handle exception
			
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	private ResponseEntity getCategory(Long categoryId) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

			Optional<Category> category = categoryRepository.findById(categoryId);
			
			if (category.isEmpty()) {
				response.setMessage(String.format("Category with id %s does not exist", categoryId));
				return ResponseEntity.notFound().build();
			}
			
			CategoryResponseFull data = new CategoryResponseFull();
			
			data.setId(category.get().getId());
			data.setName(category.get().getName());
			data.setDescription(category.get().getDescription());
			data.setCreatedAt(category.get().getCreatedAt());
			data.setUpdatedAt(category.get().getUpdatedAt());
			
			List<QuestionResponse> questionList = new ArrayList<>();
			
			List<Question> questionsByCategory = questionRepository.getQuestionsForCategory(categoryId);
			
			questionsByCategory.forEach(question -> {
				QuestionResponse questionResponse = new QuestionResponse();
				questionResponse.setId(question.getId());
				questionResponse.setQuestion(question.getQuestion());
				questionResponse.setCreatedAt(question.getCreatedAt());
				questionResponse.setUpdatedAt(question.getUpdatedAt());
				
				questionList.add(questionResponse);
			});
			
			data.setQuestions(questionList);
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(data);
			
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			// TODO: handle exception
			
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	private ResponseEntity updateCategory(Long categoryId, String name, String description) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

			Optional<Category> category = categoryRepository.findById(categoryId);
		
			if (category.isEmpty()) {
				response.setMessage(String.format("Category with id %s does not exist", categoryId));
				return ResponseEntity.notFound().build();
			}
			
			
			if (name != null && name.length() > 0 && !Objects.equals(category.get().getName(), name)) {
				category.get().setDescription(description);
			}
			
			if (description != null && description.length() > 0 && !Objects.equals(category.get().getDescription(), description)) {
				Optional<Category> categoryByName = categoryRepository.findCategoryByName(name);
				if (categoryByName.isPresent()) {
					response.setMessage(String.format("Category name %s already taken", name));
					return ResponseEntity.notFound().build();
				}
				category.get().setName(name);
			}

			categoryRepository.save(category.get());
			
			return ResponseEntity.ok().body(response);

			
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
		
		
		
	}
	
	private ResponseEntity deleteCategory(Long categoryId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		boolean exists = categoryRepository.existsById(categoryId);
		
		if (!exists) {
			response.setMessage(String.format("Category with id %s does not exist", categoryId));
			return ResponseEntity.notFound().build();
		}
		categoryRepository.deleteById(categoryId);
		
		return ResponseEntity.ok().body(response);

	}

}
