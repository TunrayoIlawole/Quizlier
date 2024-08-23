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
			
			userSession.addAnsweredQuestion(nextQuestion.getId());
			
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
		userScore.setScore(userScore.getScore());

		if (userSession.getScore() > userSession.getHighest_score()) {
			userSession.setHighest_score(userSession.getScore());
			authService.sendUserHighscore(userSession.getUsername(), String.valueOf(userSession.getScore()));
		}

		return userScore;

	}
	
	

}
