package com.quizlier.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quizlier.common.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	@Query("SELECT u FROM User u WHERE u.email = :email")
	Optional<User> findUserByEmail(@Param("email") String email);
	
	@Query("SELECT u FROM User u WHERE u.username = :username")
	Optional<User> findUserByUsername(@Param("username") String username);
}
