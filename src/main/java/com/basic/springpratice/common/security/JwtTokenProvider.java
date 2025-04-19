package com.basic.springpratice.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String secretKeyBase64;

    /** 토큰 유효시간: 1시간 */
    @Value("${security.jwt.expire-length:3600000}")
    private long validityInMilliseconds;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /** JWT 생성 */
    public String createToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰에서 인증 정보 조회 */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        List<String> roles = claims.get("roles", List.class);

        var authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        var principal = new org.springframework.security.core.userdetails.User(
                claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /** 토큰에서 회원 식별 정보(주제) 추출 */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public long getValidityInMilliseconds(String token) {
        return parseClaims(token).getExpiration().getTime();
    }

    /** 토큰 유효성 검사 */
    public boolean validateToken(String token) {
        log.info("validateToken => {}", token);
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.info("[username = {}]", claimsJws.getBody().getSubject());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        log.info("parseClaims => {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
