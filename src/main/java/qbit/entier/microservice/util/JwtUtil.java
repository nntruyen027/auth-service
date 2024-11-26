package qbit.entier.microservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey())
                .compact();
    }

//    private SecretKey getSignInKey() {
//    	 byte[] bytes = Base64.getDecoder()
//    			 .decode(secretKey.getBytes(StandardCharsets.UTF_8));
//    	return new SecretKeySpec(bytes, "HmacSHA256"); 
//    	}
   
    private SecretKey getSignInKey() {
        try {
            byte[] bytes = Base64.getDecoder().decode(secretKey.getBytes());
            return new SecretKeySpec(bytes, "HmacSHA256");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 input for secretKey", e);
        }
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

    
    private Claims extractAllClaims(String token){
    	 return Jwts.parser()
	    	.verifyWith(getSignInKey())
	    	.build()
	    	.parseSignedClaims(token)
	    	.getPayload();
    	}


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }
}
