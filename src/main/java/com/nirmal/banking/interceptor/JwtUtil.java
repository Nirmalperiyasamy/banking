package com.nirmal.banking.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@Service
public class JwtUtil {

    @Value("${my.secret.string}")
    protected String secret;

    public String generateToken(String uid) {
        Map<String, Object> claim = new HashMap<>();
        return createToken(claim, uid);
    }

    private String createToken(Map<String, Object> claim, String uid) {
        Date date = new Date(System.currentTimeMillis());
        return Jwts
                .builder()
                .setClaims(claim)
                .setSubject(uid)
                .setIssuedAt(date)
                .setExpiration(addTime(date))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private Date addTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int seconds = 6000000;
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String name = extractUsername(token);
        return (name.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
