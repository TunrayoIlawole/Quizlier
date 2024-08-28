package com.quizlier.core.mappers;

import com.quizlier.common.dto.QuestionResponse;
import com.quizlier.common.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionResponse questionToQuestionresponse(Question question);
}
