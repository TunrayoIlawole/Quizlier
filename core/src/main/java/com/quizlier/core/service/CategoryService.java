package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.quizlier.core.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.common.dto.CategoryResponse;
import com.quizlier.common.entity.Category;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	private final CategoryMapper categoryMapper;

	public CategoryResponse createCategory(CategoryRequest request) throws DuplicateEntityException {
			Optional<Category> categoryByName = categoryRepository.findCategoryByName(request.getName());
			
			if (categoryByName.isPresent()) {
				throw new DuplicateEntityException(ServiceMessages.duplicateEntity("Category"));
			}
			Category category = categoryMapper.categoryRequestToCategory(request);
			category.setCreatedAt(Calendar.getInstance().getTime());
			categoryRepository.save(category);

			CategoryResponse categoryResponse = categoryMapper.categoryToCategoryresponse(category);
			
			return categoryResponse;
	}
	
	public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        List<CategoryResponse> responseList = new ArrayList<>();

        categories.forEach(category -> {
            CategoryResponse data = categoryMapper.categoryToCategoryresponse(category);
            responseList.add(data);
        });
        return responseList;
    }
	
	public CategoryResponse getCategory(Long categoryId) throws InvalidEntityException {
        Optional<Category> category = categoryRepository.findById(categoryId);

        if (category.isEmpty()) {
            throw new InvalidEntityException(ServiceMessages.invalidEntity("Category", categoryId.toString()));
        }

		CategoryResponse categoryResponse = categoryMapper.categoryToCategoryresponse(category.get());

        return categoryResponse;

    }
	
	public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) throws InvalidEntityException, DuplicateEntityException {
		Optional<Category> category = categoryRepository.findById(categoryId);
		
			if (category.isEmpty()) {
				throw new InvalidEntityException(ServiceMessages.invalidEntity("Category", categoryId.toString()));
			}
			
			if (!categoryRequest.getDescription().isEmpty() && !Objects.equals(category.get().getDescription(), categoryRequest.getDescription())) {
				category.get().setDescription(categoryRequest.getDescription());
			}
			
			if (!categoryRequest.getName().isEmpty() && !Objects.equals(category.get().getName(), categoryRequest.getName())) {
				Optional<Category> categoryByName = categoryRepository.findCategoryByName(categoryRequest.getName());
				if (categoryByName.isPresent()) {
					throw new DuplicateEntityException(ServiceMessages.duplicateEntity("Category"));
				}
				category.get().setName(categoryRequest.getName());
			}
			categoryRepository.save(category.get());

			CategoryResponse categoryResponse = categoryMapper.categoryToCategoryresponse(category.get());
			
			return categoryResponse;
	}
	
	public void deleteCategory(Long categoryId) throws InvalidEntityException {
		boolean exists = categoryRepository.existsById(categoryId);
		
		if (!exists) {
			throw new InvalidEntityException(ServiceMessages.invalidEntity("Category", categoryId.toString()));
		}
		categoryRepository.deleteById(categoryId);
	}

	public Category getSingleCategory(Long id) {
		Optional<Category> category = categoryRepository.findById(id);

		return category.orElse(null);
	}
}
