package com.quizlier.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quizlier.common.entity.Question;
import com.quizlier.core.service.QuizService;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("api/v1/quiz")
public class QuizController {
	
	@Autowired
	private QuizService quizService;
	
	@GetMapping(path = "/{category}/next")
	public Question getNextQuestion(@PathVariable String categoryId ) {
		Long categoryIdLong = Long.valueOf(categoryId);
		return quizService.getNextQuestion(categoryIdLong);
	}

}
