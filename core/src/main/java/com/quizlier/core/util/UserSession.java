package com.quizlier.core.util;

import java.util.HashSet;

public class UserSession {
	
	HashSet<Long> answeredQuestionsIds = new HashSet<Long>();
	
	public HashSet<Long> getAnsweredQuestions() {
		return answeredQuestionsIds;
	}
	
	public void addAnsweredQuestion(Long questionId) {
		answeredQuestionsIds.add(questionId);
	}

}
