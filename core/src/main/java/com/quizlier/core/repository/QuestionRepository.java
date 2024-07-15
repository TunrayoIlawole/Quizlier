package com.quizlier.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quizlier.common.entity.Question;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
	
	@Query("SELECT u FROM Question u WHERE u.category.id = :categoryId")
	List<Question> getQuestionsForCategory(@Param("categoryId") Long categoryId);
	
	@Query("SELECT u FROM Question u WHERE u.question = :question")
	Optional<Question> getQuestion(@Param("question") String question);

}
