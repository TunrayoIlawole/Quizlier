package com.quizlier.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quizlier.core.entity.Option;
import com.quizlier.core.entity.Question;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
	@Query("SELECT u FROM Option u WHERE u.question.id = :questionId")
	List<Option> getOptionsForQuestions(@Param("questionId") Long questionId);
}
