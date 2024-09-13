package com.quizlier.core.controllers;

import com.quizlier.common.dto.AnswerSubmission;
import com.quizlier.common.dto.UserScore;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quizlier.common.entity.Question;
import com.quizlier.core.service.QuizService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("api/v1/quiz")
@RequiredArgsConstructor
public class QuizController {
	private final QuizService quizService;
	
	@GetMapping(path = "/{categoryId}/next")
	public ResponseEntity getNextQuestion(@PathVariable String categoryId ) {
		Long categoryIdLong = Long.valueOf(categoryId);
		Question nextQuestion = quizService.getNextQuestion(categoryIdLong);

		return ResponseEntity.ok(nextQuestion);
	}

	@PostMapping(path = "/submit")
	public ResponseEntity submitAnswer(AnswerSubmission answerSubmission) {
		UserScore userScore = quizService.submitAnswer(answerSubmission);

		return ResponseEntity.ok(userScore);
	}

	@GetMapping(path = "/endgame")
	public ResponseEntity endGame() {
		Integer finalScore = quizService.endQuiz();

		return ResponseEntity.ok(finalScore);
	}

}
