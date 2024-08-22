package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.repository.CategoryRepository;
import com.quizlier.core.repository.QuestionRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	private final QuestionRepository questionRepository;
	
	
	public CategoryResponse createCategory(CategoryRequest request) throws DuplicateEntityException {
			Optional<Category> categoryByName = categoryRepository.findCategoryByName(request.getName());
			
			if (categoryByName.isPresent()) {
				throw new DuplicateEntityException(String.format("Category with name %s already exists", request.getName()));
			}
			Category category = new Category();
			category.setName(request.getName());
			category.setDescription(request.getDescription());
			category.setCreatedAt(Calendar.getInstance().getTime());
			categoryRepository.save(category);

			CategoryResponse categoryResponse = new CategoryResponse();
			categoryResponse.setId(category.getId());
			categoryResponse.setName(request.getName());
			categoryResponse.setDescription(request.getDescription());
			
			return categoryResponse;
	}
	
	public List<CategoryResponse> getAllCategories() throws Exception {
        List<Category> categories = categoryRepository.findAll();

        List<CategoryResponse> responseList = new ArrayList<>();

        categories.forEach(category -> {
            CategoryResponse data = new CategoryResponse();
            data.setId(category.getId());
            data.setName(category.getName());
            data.setDescription(category.getDescription());

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

        List<QuestionResponse> questionList = new ArrayList<>();

		// Have method in question service that does this
        List<Question> questionsByCategory = questionRepository.getQuestionsForCategory(categoryId);

        questionsByCategory.forEach(question -> {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setId(question.getId());
            questionResponse.setQuestion(question.getQuestion());

            questionList.add(questionResponse);
        });

        data.setQuestions(questionList);

        return data;

    }
	
	public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) throws InvalidEntityException, DuplicateEntityException {
		System.out.println("line 104 - works");
		Optional<Category> category = categoryRepository.findById(categoryId);
		
			if (category.isEmpty()) {
				throw new InvalidEntityException(String.format("Category with id %s does not exist", categoryId));
			}
			
			if (!categoryRequest.getDescription().isEmpty() && !Objects.equals(category.get().getDescription(), categoryRequest.getDescription())) {
				System.out.println("line 111 - works");
				category.get().setDescription(categoryRequest.getDescription());
			}
			
			if (!categoryRequest.getName().isEmpty() && !Objects.equals(category.get().getName(), categoryRequest.getName())) {
				Optional<Category> categoryByName = categoryRepository.findCategoryByName(categoryRequest.getName());
				if (categoryByName.isPresent()) {
					throw new DuplicateEntityException(String.format("Category name %s already taken", categoryRequest.getName()));
				}
				category.get().setName(categoryRequest.getName());
			}
			categoryRepository.save(category.get());

			CategoryResponse categoryResponse = new CategoryResponse();
			categoryResponse.setId(category.get().getId());
			categoryResponse.setName(categoryRequest.getName());
			categoryResponse.setDescription(categoryRequest.getDescription());
			
			return categoryResponse;
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
