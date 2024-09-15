package com.quizlier.core.mappers;

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.entity.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    Option optionRequestToOption(OptionRequest optionRequest);
    OptionResponse optionToOptionresponse(Option option);
}
