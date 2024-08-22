package com.quizlier.core.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.quizlier.common.dto.AnswerSubmission;
import com.quizlier.common.dto.UserScore;
import com.quizlier.common.entity.Option;
import com.quizlier.common.entity.Question;
import com.quizlier.core.repository.OptionRepository;
import com.quizlier.core.repository.QuestionRepository;
import com.quizlier.core.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {

	private final QuestionRepository questionRepository;
	private final OptionRepository optionRepository;

	private final QuestionService questionService;

	private final UserSession userSession;

	private final AuthService authService;

	public Question getNextQuestion(Long activeCategory) {

		// question service
		List<Question> questionsByCategory = questionRepository.getQuestionsForCategory(activeCategory);
		
		List<Question> availableQuestions = questionsByCategory.stream().filter(question -> 
			!userSession.getAnsweredQuestions().contains(question.getId())
		).collect(Collectors.toList());
		
		if (!availableQuestions.isEmpty()) {
			Question nextQuestion = availableQuestions.get(0);
			
			userSession.addAnsweredQuestion(nextQuestion.getId());
			
			return nextQuestion;
		} else {
			return null;
		}
		
	}

	public UserScore submitAnswer(AnswerSubmission answerSubmission) {
		Optional<Option> currentOption = optionRepository.findById(answerSubmission.getSelectedOption());

		if (currentOption.isPresent() && currentOption.get().getIsCorrect() && currentOption.get().getQuestion().getId().equals(answerSubmission.getQuestionId())) {
			userSession.incrementScore();
		}

		UserScore userScore = new UserScore();
		userScore.setScore(userScore.getScore());

		if (userSession.getScore() > userSession.getHighest_score()) {
			userSession.setHighest_score(userSession.getScore());
			authService.sendUserHighscore(userSession.getUsername(), String.valueOf(userSession.getScore()));
		}

		return userScore;

	}
	
	

}
