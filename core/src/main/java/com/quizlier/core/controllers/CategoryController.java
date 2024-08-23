package com.quizlier.core.controllers;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.common.dto.CategoryResponse;
import com.quizlier.common.dto.CategoryResponseFull;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);

        ResponseData<CategoryResponse> response = new ResponseData<CategoryResponse>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
        response.setData(category);

        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();

        ResponseData<List<CategoryResponse>> response = new ResponseData<List<CategoryResponse>>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
        response.setData(categories);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping(path = "{categoryId}")
    public ResponseEntity getCategory(@PathVariable Long categoryId) {

        CategoryResponseFull category = categoryService.getCategory(categoryId);

        ResponseData<CategoryResponseFull> response = new ResponseData<CategoryResponseFull>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);

        response.setData(category);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(path = "{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);

        ResponseData response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping(path = "{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest categoryRequest) {

        CategoryResponse category = categoryService.updateCategory(categoryId, categoryRequest);

        ResponseData<CategoryResponse> response = new ResponseData<CategoryResponse>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);

        response.setData(category);
        return ResponseEntity.ok().body(response);

    }


}
