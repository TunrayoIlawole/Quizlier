package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.common.dto.CategoryResponse;
import com.quizlier.common.dto.CategoryResponseFull;
import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.entity.Category;
import com.quizlier.common.entity.Question;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.repository.CategoryRepository;
import com.quizlier.core.repository.QuestionRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	
	public Category createCategory(CategoryRequest request) throws DuplicateEntityException {
			Optional<Category> categoryByName = categoryRepository.findCategoryByName(request.getName());
			
			if (categoryByName.isPresent()) {
				throw new DuplicateEntityException(String.format("Category with name %s already exists", request.getName()));
			}
			Category category = new Category();
			category.setName(request.getName());
			category.setDescription(request.getDescription());
			category.setCreatedAt(Calendar.getInstance().getTime());
			
			categoryRepository.save(category);
			
			return category;
	}
	
	public List<CategoryResponse> getAllCategories() throws Exception {
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
        return responseList;
    }
	
	public CategoryResponseFull getCategory(Long categoryId) throws InvalidEntityException {
        Optional<Category> category = categoryRepository.findById(categoryId);

        if (category.isEmpty()) {
            throw new InvalidEntityException(String.format("Category with id %s does not exist", categoryId));
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

        return data;

    }
	
	public Category updateCategory(Long categoryId, String name, String description) throws InvalidEntityException, DuplicateEntityException {
			Optional<Category> category = categoryRepository.findById(categoryId);
		
			if (category.isEmpty()) {
				throw new InvalidEntityException(String.format("Category with id %s does not exist", categoryId));
			}
			
			if (name != null && name.length() > 0 && !Objects.equals(category.get().getName(), name)) {
				category.get().setDescription(description);
			}
			
			if (description != null && description.length() > 0 && !Objects.equals(category.get().getDescription(), description)) {
				Optional<Category> categoryByName = categoryRepository.findCategoryByName(name);
				if (categoryByName.isPresent()) {
					throw new DuplicateEntityException(String.format("Category name %s already taken", name));
				}
				category.get().setName(name);
			}
			categoryRepository.save(category.get());
			
			return category.get();
	}
	
	public void deleteCategory(Long categoryId) throws InvalidEntityException {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		boolean exists = categoryRepository.existsById(categoryId);
		
		if (!exists) {
			throw new InvalidEntityException(String.format("Category with id %s does not exist", categoryId));
		}
		categoryRepository.deleteById(categoryId);
	}
}
