package com.quizlier.core.service;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.common.dto.CategoryResponse;
import com.quizlier.common.entity.Category;
import com.quizlier.core.AbstractIntegrationTest;
import com.quizlier.core.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void createCategory_Success() {

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("test category");
        categoryRequest.setDescription("test category description");

        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);

        assertNotNull(categoryResponse);

        Optional<Category> savedCategory = categoryRepository.findCategoryByName(categoryRequest.getName());
        assertTrue(savedCategory.isPresent());
        assertEquals("test category", savedCategory.get().getName());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void getCategories_Success() {

        List<CategoryResponse> categories = categoryService.getAllCategories();

        assertNotNull(categories);
        assertNotEquals(0, categories.size());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void getCategory_Success() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("test category");
        categoryRequest.setDescription("test category description");

        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);

        CategoryResponse savedCategory = categoryService.getCategory(categoryResponse.getId());

        assertNotNull(savedCategory);
        assertEquals("test category", savedCategory.getName());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void updateCategory_Success() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("test category");
        categoryRequest.setDescription("test category description");

        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);

        CategoryRequest updateCategoryRequest = new CategoryRequest();
        updateCategoryRequest.setName("testing category");
        updateCategoryRequest.setDescription("test category description");

        CategoryResponse updateCategoryResponse = categoryService.updateCategory(categoryResponse.getId(), updateCategoryRequest);

        assertNotNull(updateCategoryResponse);

        Optional<Category> updatedCategory = categoryRepository.findById(categoryResponse.getId());

        assertTrue(updatedCategory.isPresent());
        assertEquals("testing category", updatedCategory.get().getName());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void deleteCategory_Success() {

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("test category");
        categoryRequest.setDescription("test category description");

        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);

        categoryService.deleteCategory(categoryResponse.getId());

        Optional<Category> deletedCategory = categoryRepository.findById(categoryResponse.getId());
        assertFalse(deletedCategory.isPresent());
    }

}
