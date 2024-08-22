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

public class QuizService {

	private QuestionRepository questionRepository;
	private OptionRepository optionRepository;

	private QuestionService questionService;

	private UserSession userSession;

	private AuthService authService;

	public QuizService(QuestionRepository questionRepository, OptionRepository optionRepository,
					   QuestionService questionService, UserSession userSession, AuthService authService) {
		this.questionRepository = questionRepository;
		this.optionRepository = optionRepository;
		this.questionService = questionService;
		this.userSession = userSession;
		this.authService = authService;
	}

	
//	public void playGame(Long activeCategoryId) {
//
//		// Create game log record
//
//	}
		
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
