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

import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.common.dto.OptionRequest;
import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.entity.Option;
import com.quizlier.common.entity.Question;
import com.quizlier.core.repository.OptionRepository;
import com.quizlier.core.repository.QuestionRepository;

@Service
public class OptionService {
	
	@Autowired
	private OptionRepository optionRepository;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	public Option createOption(OptionRequest request, Long questionId) throws InvalidEntityException, DuplicateEntityException {
		try {
			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
			}
			
			Option existingOption = null;
			
			List<Option> options = optionRepository.getOptionsForQuestions(questionId);
			
			for (Option option : options) {
				if (option.getOption_text().equals(request.getOptionText())) {
					existingOption = option;
					break;
				}
			}
			
			if (existingOption == null || !existingOption.getQuestion().equals(question.get())) {
				Option option = new Option();
				
				option.setOption_text(request.getOptionText());
				option.setIsCorrect(request.isCorrect());
				option.setCreatedAt(Calendar.getInstance().getTime());
				option.setQuestion(question.get());
				
				optionRepository.save(option);

				return option;
			} else {
				throw new DuplicateEntityException("Option already exists for this question");
			}

		} catch (Exception e) {
			throw e;

		}
	}
	
	public List<OptionResponse> getAllOptionsByQuestions(Long questionId) throws InvalidEntityException {
		try {
			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
			}

			List<Option> options = optionRepository.getOptionsForQuestions(questionId);
			
		    List<OptionResponse> responseList = new ArrayList<>();

		    options.forEach(option -> {
		    	OptionResponse optionResponse = new OptionResponse();
		    	optionResponse.setId(option.getId());
		    	optionResponse.setOptionText(option.getOption_text());
		    	optionResponse.setCorrect(option.getIsCorrect());
		    	optionResponse.setCreatedAt(option.getCreatedAt());
		    	optionResponse.setUpdatedAt(option.getUpdatedAt());
		    	
		    	responseList.add(optionResponse);
		    });
			return responseList;

		} catch (Exception e) {
			throw e;

		}
	}
	
	public Option updateOption(Long optionId, String optionText, Boolean isCorrect) throws InvalidEntityException {
		try {
			Optional<Option> optionById = optionRepository.findById(optionId);
		
			if (optionById.isEmpty()) {
				throw new InvalidEntityException(String.format("Option with id %s does not exist", optionId));
			}
			
			if (isCorrect != null) {
				optionById.get().setIsCorrect(isCorrect);
			}
			
			if (optionText != null) {
				Option existingOption = null;
				
				List<Option> options = optionRepository.getOptionsForQuestions(optionById.get().getQuestion().getId());

				for (Option option : options) {
					if (option.getOption_text().equals(optionText)) {
						existingOption = option;
						break;
					}
				}
				
				if (existingOption == null || (existingOption != null && !Objects.equals(existingOption.getQuestion(), optionById.get().getQuestion()) || (existingOption != null && Objects.equals(existingOption.getId(), optionById.get().getId())))) {
					optionById.get().setOption_text(optionText);
				}
			}
			
			optionById.get().setCreatedAt(Calendar.getInstance().getTime());
			optionRepository.save(optionById.get());
			
			return optionById.get();
			
		} catch (Exception e) {
			throw e;

		}
	}
	
	public void deleteOption(Long optionId) throws InvalidEntityException {
		boolean exists = optionRepository.existsById(optionId);
		
		if (!exists) {
			throw new InvalidEntityException(String.format("Option with id %s does not exist", optionId));
		}
		optionRepository.deleteById(optionId);
	}

}
