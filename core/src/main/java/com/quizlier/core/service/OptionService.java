package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import com.quizlier.core.dto.OptionRequest;
import com.quizlier.core.dto.OptionResponse;
import com.quizlier.core.dto.QuestionRequest;
import com.quizlier.core.dto.QuestionResponse;
import com.quizlier.core.entity.Category;
import com.quizlier.core.entity.Option;
import com.quizlier.core.entity.Question;
import com.quizlier.core.repository.OptionRepository;
import com.quizlier.core.repository.QuestionRepository;

public class OptionService {
	
	@Autowired
	private OptionRepository optionRepository;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	public ResponseEntity createOption(OptionRequest request, Long questionId) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
			
			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				response.setMessage(null);
				return ResponseEntity.badRequest().body(response);
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
				
				response.setStatus(ServiceStatusCodes.SUCCESS);
				response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
				response.setData(option);
			} else {
				response.setMessage("Option already exists for this question");
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	public ResponseEntity getAllOptionsByQuestions(Long questionId) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
			
			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				response.setMessage(null);
				return ResponseEntity.badRequest().body(response);
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
			
			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setData(responseList);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			
			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			// TODO: handle exception
			
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}
	
	public ResponseEntity updateOption(Long optionId, String optionText, Boolean isCorrect) {
		try {
			ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

			Optional<Option> optionById = optionRepository.findById(optionId);
		
			if (optionById.isEmpty()) {
				response.setMessage(String.format("Option with id %s does not exist", optionId));
				return ResponseEntity.notFound().build();
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
			
			return ResponseEntity.ok().body(response);
			
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
		
		
		
	}
	
	public ResponseEntity deleteOption(Long optionId) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		boolean exists = optionRepository.existsById(optionId);
		
		if (!exists) {
			response.setMessage(String.format("Option with id %s does not exist", optionId));
			return ResponseEntity.notFound().build();
		}
		optionRepository.deleteById(optionId);
		
		return ResponseEntity.ok().body(response);

	}

}
