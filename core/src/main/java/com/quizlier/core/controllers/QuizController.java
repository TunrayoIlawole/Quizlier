package com.quizlier.core.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quizlier.common.entity.Question;
import com.quizlier.core.service.QuizService;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("api/v1/quiz")
@RequiredArgsConstructor
public class QuizController {
	private final QuizService quizService;
	
	@GetMapping(path = "/{categoryId}/next")
	public Question getNextQuestion(@PathVariable String categoryId ) {
		Long categoryIdLong = Long.valueOf(categoryId);
		return quizService.getNextQuestion(categoryIdLong);
	}

}
