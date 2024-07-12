package com.quizlier.auth.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.quizlier.common.entity.User;
import com.quizlier.common.entity.UserRole;


public class UserInfoDetails implements UserDetails {
	
	private String name;
	private String password;
	private List<GrantedAuthority> authorities; 
	
	public UserInfoDetails(User user) {
		name = user.getUsername();
		password = user.getPassword();
		
//		authority = new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name());
		
		authorities = Arrays.asList(user.getUserRole())
				.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
				.collect(Collectors.toList());
				
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return name;
	}
	
    @Override
    public boolean isAccountNonExpired() { 
        return true; 
    } 
  
    @Override
    public boolean isAccountNonLocked() { 
        return true; 
    } 
  
    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    } 
  
    @Override
    public boolean isEnabled() { 
        return true; 
    } 

}
