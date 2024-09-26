package com.quizlier.core.service;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.common.dto.CategoryResponse;
import com.quizlier.common.entity.Category;
import com.quizlier.core.AbstractMockitoJUnitTest;
import com.quizlier.core.TestUtil;
import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.mappers.CategoryMapper;
import com.quizlier.core.repository.CategoryRepository;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CategoryServiceTest extends AbstractMockitoJUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Sports");
        categoryRequest.setDescription(RandomStringUtils.randomAlphanumeric(56));

        when(categoryRepository.findCategoryByName(categoryRequest.getName())).thenReturn(Optional.empty());

        Category category = TestUtil.buildCategory(1L);
        when(categoryMapper.categoryRequestToCategory(any(CategoryRequest.class))).thenReturn(category);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName(categoryRequest.getName());
        categoryResponse.setDescription(categoryRequest.getDescription());

        when(categoryMapper.categoryToCategoryresponse(any(Category.class))).thenReturn(categoryResponse);

        var response = categoryService.createCategory(categoryRequest);

        Assert.notNull(response);
    }

    @Test
    void createCategory_duplicateCategory() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName(RandomStringUtils.randomAlphanumeric(9));
        categoryRequest.setDescription(RandomStringUtils.randomAlphanumeric(56));

        Category category = new Category();
        category.setId(4L);
        category.setName(RandomStringUtils.randomAlphabetic(9));
        category.setDescription(RandomStringUtils.randomAlphanumeric(56));
        category.setCreatedAt(new Date());

        when(categoryRepository.findCategoryByName(categoryRequest.getName())).thenReturn(Optional.of(category));

        var response = assertThrows(DuplicateEntityException.class, () -> categoryService.createCategory(categoryRequest));

        assertEquals("Category already exists", response.getMessage());
    }

    @Test
    void getAllCategories() {
        List<Category> categories = new ArrayList<>();

        Category category = new Category();
        category.setId(4L);
        category.setName(RandomStringUtils.randomAlphabetic(9));
        category.setDescription(RandomStringUtils.randomAlphanumeric(56));
        category.setCreatedAt(new Date());

        Category category1 = new Category();
        category1.setId(8L);
        category1.setName(RandomStringUtils.randomAlphabetic(9));
        category1.setDescription(RandomStringUtils.randomAlphanumeric(56));
        category1.setCreatedAt(new Date());

        categories.add(category);
        categories.add(category1);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryResponse> categoryResponseList = new ArrayList<>();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        categoryResponse.setDescription(category.getDescription());

        CategoryResponse categoryResponse1 = new CategoryResponse();
        categoryResponse1.setId(category1.getId());
        categoryResponse1.setName(category1.getName());
        categoryResponse1.setDescription(category1.getDescription());

        categoryResponseList.add(categoryResponse);
        categoryResponseList.add(categoryResponse1);

        var response = categoryService.getAllCategories();

        assertNotNull(response);
        assertNotEquals(0, response.size());

    }

    @Test
    void getCategory() {
        Long categoryId = 4L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName(RandomStringUtils.randomAlphabetic(9));
        category.setDescription(RandomStringUtils.randomAlphanumeric(56));
        category.setCreatedAt(new Date());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        categoryResponse.setDescription(category.getDescription());

        var response = categoryService.getCategory(categoryId);

        assertNotNull(response);
    }

    @Test
    void getCategory_invalidCategory() {
        Long categoryId = 89L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        var response = assertThrows(InvalidEntityException.class, () -> categoryService.getCategory(categoryId));

        assertEquals("Category with id '89' does not exist", response.getMessage());
    }

    @Test
    void updateCategory() {
        Long categoryId = 4L;

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName(RandomStringUtils.randomAlphabetic(9));
        categoryRequest.setDescription("New Description");

        Category category = TestUtil.buildCategory(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setName(categoryRequest.getName());
        categoryResponse.setDescription(categoryRequest.getDescription());

        when(categoryMapper.categoryToCategoryresponse(any(Category.class))).thenReturn(categoryResponse);

        var response = categoryService.updateCategory(categoryId, categoryRequest);

        assertNotNull(response);
        assertEquals(categoryResponse.getName(), categoryRequest.getName());
        assertEquals(categoryResponse.getDescription(), categoryRequest.getDescription());
    }

    @Test
    void updateCategory_invalidCategory() {
        Long categoryId = 89L;

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName(RandomStringUtils.randomAlphabetic(9));
        categoryRequest.setDescription("New Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        var response = assertThrows(InvalidEntityException.class, () -> categoryService.updateCategory(categoryId, categoryRequest));

        assertEquals("Category with id '89' does not exist", response.getMessage());
    }

    @Test
    void updateCategory_duplicateName() {
        Long categoryId = 4L;

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("New name");
        categoryRequest.setDescription(RandomStringUtils.randomAlphabetic(90));

        Category category = new Category();
        category.setId(categoryId);
        category.setName(RandomStringUtils.randomAlphabetic(9));
        category.setDescription(RandomStringUtils.randomAlphanumeric(56));
        category.setCreatedAt(new Date());

        Category existingCategory = new Category();
        existingCategory.setId(67L);
        existingCategory.setName("New name");
        existingCategory.setDescription(RandomStringUtils.randomAlphanumeric(56));
        existingCategory.setCreatedAt(new Date());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findCategoryByName(categoryRequest.getName())).thenReturn(Optional.of(existingCategory));


        var response = assertThrows(DuplicateEntityException.class, () -> categoryService.updateCategory(categoryId, categoryRequest));

        assertEquals("Category already exists", response.getMessage());
    }

//    @Test
    void deleteCategory() {
        Long categoryId = 4L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName(RandomStringUtils.randomAlphabetic(9));
        category.setDescription(RandomStringUtils.randomAlphanumeric(56));
        category.setCreatedAt(new Date());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
//
//        var response = categoryService.deleteCategory(categoryId);
//
//        assertNull(response);
    }

    @Test
    void deleteCategory_invalidCategory() {
        Long categoryId = 89L;

//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        var response = assertThrows(InvalidEntityException.class, () -> categoryService.deleteCategory(categoryId));

        assertEquals("Category with id '89' does not exist", response.getMessage());
    }
}