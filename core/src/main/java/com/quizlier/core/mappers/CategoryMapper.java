package com.quizlier.core.mappers;

import com.quizlier.common.dto.CategoryRequest;
import com.quizlier.common.dto.CategoryResponse;
import org.mapstruct.Mapper;
import com.quizlier.common.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse categoryToCategoryresponse(Category category);
    Category categoryRequestToCategory(CategoryRequest categoryRequest);
}
