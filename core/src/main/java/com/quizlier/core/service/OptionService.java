package com.quizlier.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.quizlier.core.exceptions.DuplicateEntityException;
import com.quizlier.core.exceptions.InvalidEntityException;
import com.quizlier.core.exceptions.MaximumEntityException;
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
	
	public OptionResponse createOption(OptionRequest request, Long questionId) throws InvalidEntityException, DuplicateEntityException, MaximumEntityException {
		try {

			Optional<Question> question = questionRepository.findById(questionId);
			
			if (question.isEmpty()) {
				throw new InvalidEntityException(String.format("Question with id %s does not exist", questionId));
			}
			
			Option existingOption = null;
			
			List<Option> options = optionRepository.getOptionsForQuestions(questionId);

			if (options.size() > 3) {
				throw new MaximumEntityException("Maximum number of options for question exceeded");
			}
			
			for (Option option : options) {
				if (option.getOption_text().equals(request.getOptionText())) {
					existingOption = option;
					break;
				}
			}
			
			if (existingOption == null) {
				if (request.getIsCorrect()) {
					Optional<Option> correctAnswer = options.stream().filter(Option::getIsCorrect).findFirst();

					// If there is already an option set as the correct answer
					if (correctAnswer.isPresent()) {
						correctAnswer.get().setIsCorrect(false);
						optionRepository.save(correctAnswer.get());
					}
				}
				Option option = new Option();
				
				option.setOption_text(request.getOptionText());
				option.setIsCorrect(request.getIsCorrect());
				option.setCreatedAt(Calendar.getInstance().getTime());
				option.setQuestion(question.get());
				
				optionRepository.save(option);

				OptionResponse optionResponse = new OptionResponse();
				optionResponse.setId(option.getId());
				optionResponse.setQuestionId(questionId);
				optionResponse.setOptionText(request.getOptionText());
				optionResponse.setIsCorrect(request.getIsCorrect());

				return optionResponse;
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

			boolean correctAnswer = options.stream().anyMatch(Option::getIsCorrect);

			if (!correctAnswer) {
				throw new InvalidEntityException(String.format("Question %s does not have a correct answer set", questionId));
			}
			
		    List<OptionResponse> responseList = new ArrayList<>();

		    options.forEach(option -> {
		    	OptionResponse optionResponse = new OptionResponse();
		    	optionResponse.setId(option.getId());
		    	optionResponse.setOptionText(option.getOption_text());
		    	optionResponse.setIsCorrect(option.getIsCorrect());
		    	
		    	responseList.add(optionResponse);
		    });
			return responseList;

		} catch (Exception e) {
			throw e;

		}
	}
	
	public OptionResponse updateOption(Long optionId, OptionRequest optionRequest) throws InvalidEntityException, DuplicateEntityException {
		try {
			Optional<Option> optionById = optionRepository.findById(optionId);
		
			if (optionById.isEmpty()) {
				throw new InvalidEntityException(String.format("Option with id %s does not exist", optionId));
			}
			Question question = optionById.get().getQuestion();

			if (optionRequest.getIsCorrect()) {
				List<Option> optionsByQuestions = optionRepository.getOptionsForQuestions(question.getId());

				Optional<Option> correctAnswer = optionsByQuestions.stream().filter(Option::getIsCorrect).findFirst();

				// If there is already an option set as the correct answer and it is not equal to the option that is being edited to be the correct answer:
				if (correctAnswer.isPresent() && !Objects.equals(correctAnswer.get().getId(), optionId)) {
					correctAnswer.get().setIsCorrect(false);
					optionRepository.save(correctAnswer.get());
				}
			}

			optionById.get().setIsCorrect(optionRequest.getIsCorrect());
			
			if (optionRequest.getOptionText() != null) {
				Option existingOption = null;
				
				List<Option> options = optionRepository.getOptionsForQuestions(optionById.get().getQuestion().getId());

				for (Option option : options) {
					if (option.getOption_text().equals(optionRequest.getOptionText())) {
						existingOption = option;
						break;
					}
				}
				
				if (existingOption != null) {
					throw new DuplicateEntityException("Option already exists for this question");
				}
				optionById.get().setOption_text(optionRequest.getOptionText());
			}
			
			optionById.get().setUpdatedAt(Calendar.getInstance().getTime());
			optionRepository.save(optionById.get());

			OptionResponse optionResponse = new OptionResponse();
			optionResponse.setId(optionById.get().getId());
			optionResponse.setQuestionId(question.getId());
			optionResponse.setOptionText(optionRequest.getOptionText());
			optionResponse.setIsCorrect(optionRequest.getIsCorrect());

			return optionResponse;
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
