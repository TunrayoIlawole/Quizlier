package com.quizlier.core.mappers;

import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.entity.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    @Mapping(source = "option_text", target = "optionText")
    OptionResponse optionToOptionresponse(Option option);
}
