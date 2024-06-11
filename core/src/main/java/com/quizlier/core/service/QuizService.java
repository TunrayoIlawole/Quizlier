package com.quizlier.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.quizlier.common.entity.Question;
import com.quizlier.core.repository.QuestionRepository;
import com.quizlier.core.util.UserSession;

public class QuizService {
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private UserSession userSession;
	
	public void playGame(Long activeCategoryId) {
		
		// Create game log record
		
	}
		
	public Question getNextQuestion(Long activeCategory) {
		
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
	
	

}
