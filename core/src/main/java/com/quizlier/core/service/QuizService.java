package com.quizlier.core.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.quizlier.common.dto.AnswerSubmission;
import com.quizlier.common.dto.UserScore;
import com.quizlier.common.entity.Option;
import com.quizlier.common.entity.Question;

import com.quizlier.core.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
	private final OptionService optionService;

	private final QuestionService questionService;

	private final UserSession userSession;

	private final AuthService authService;

	public Question getNextQuestion(Long activeCategory) {

		List<Question> questionsByCategory = questionService.getQuestionsForCategory(activeCategory);
		
		List<Question> availableQuestions = questionsByCategory.stream().filter(question -> 
			!userSession.getAnsweredQuestions().contains(question.getId())
		).collect(Collectors.toList());
		
		if (!availableQuestions.isEmpty()) {
			Question nextQuestion = availableQuestions.get(0);

			return nextQuestion;
		} else {
			return null;
		}
		
	}

	public UserScore submitAnswer(AnswerSubmission answerSubmission) {
		Option currentOption = optionService.getOption(answerSubmission.getSelectedOption());

		if (currentOption != null && currentOption.getIsCorrect() && currentOption.getQuestion().getId().equals(answerSubmission.getQuestionId())) {
			userSession.incrementScore();
		}

		UserScore userScore = new UserScore();
		userScore.setScore(userSession.getScore());

		userSession.addAnsweredQuestion(answerSubmission.getQuestionId());

		return userScore;
	}

	public Integer endQuiz() {
		int finalScore = userSession.getScore();
		int highestScore = authService.getHighestScore(userSession.getUsername());

		if (finalScore > highestScore) {
			authService.sendUserHighscore(userSession.getUsername(), String.valueOf(finalScore));
		}

		return finalScore;

	}
	
	

}
