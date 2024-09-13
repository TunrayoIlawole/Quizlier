package com.quizlier.core.util;

import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserSession {

	private String username;
	
	private final HashSet<Long> answeredQuestionsIds = new HashSet<Long>();

	private int score = 0;

	private int highest_score;
	
	public HashSet<Long> getAnsweredQuestions() {
		return answeredQuestionsIds;
	}
	
	public void addAnsweredQuestion(Long questionId) {
		answeredQuestionsIds.add(questionId);
	}

	public int getScore() {
		return score;
	}

	public void incrementScore() {
		this.score++;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getHighest_score() {
		return highest_score;
	}

	public void setHighest_score(int highest_score) {
		this.highest_score = highest_score;
	}

}
