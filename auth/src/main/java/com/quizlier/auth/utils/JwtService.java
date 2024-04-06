package com.quizlier.auth.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	// Todo: save this as a config to the database
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    
    public String generateToken(String username) {
    	Map<String, Object> claims = new HashMap<>();
    	return createToken(claims, username);
    }
    
    private String createToken(Map<String, Object> claims, String username) {
//    	return Jwts.builder()
//    			.setClaims(claims)
//    			.setSubject(username)
//    			.setIssuedAt(new Date(System.currentTimeMillis()))
//    			.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
//    			.signWith(getSignKey(), SecureDigestAlgorithm<Key, Key>c).compact();
    	
    	return Jwts.builder().claims()
    			.add(claims)
    			.subject(username)
    			.issuedAt(new Date(System.currentTimeMillis()))
    			.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
    			.and()
    			.signWith(getSignKey())
    			.compact();
    }
    
    private SecretKey getSignKey() {
    	byte[] keyBytes = Decoders.BASE64.decode(SECRET);
    	return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String extractUsername(String token) { 
        return extractClaim(token, Claims::getSubject); 
    }
    
    public Date extractExpiration(String token) {
    	return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    	final Claims claims = extractAllClaims(token);
    	return claimsResolver.apply(claims);
    }
    
    public Claims extractAllClaims(String token) {
    	return Jwts
    			.parser()
    			.verifyWith(getSignKey())
    			.build()
    			.parseSignedClaims(token)
    			.getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
    	return extractExpiration(token).before(new Date());
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
    	final String username = extractUsername(token);
    	return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
