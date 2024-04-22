package org.tbloomfield.codingcontest.service;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

public class DequeueJWTToken {
	private static final SecretKey key = Jwts.SIG.HS256.key().build();	

	public static String generateToken(String username) {
		return Jwts.builder()
				.claim("passedQueue", true)
				.subject(username)
				.signWith(key)
				.compact();
	}
}
