package com.quizlier.core.dto;

import java.util.Date;
import java.util.List;

import com.quizlier.core.entity.Option;

public class QuestionResponseFull {
	private Long id;
	private String question;
	private Date createdAt;
	private Date updatedAt;
	
	private List<OptionResponse> options;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<OptionResponse> getOptions() {
		return options;
	}

	public void setOptions(List<OptionResponse> options) {
		this.options = options;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
