package com.basic.springpratice.domain.auth.controller;


import com.basic.springpratice.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Validated LoginRequest request) {
        try {
            Authentication authReq = new UsernamePasswordAuthenticationToken(
                    request.username(), request.password());
            Authentication auth = authenticationManager.authenticate(authReq);

            // 권한 리스트 추출
            var roles = auth.getAuthorities().stream()
                    .map(granted -> granted.getAuthority())
                    .collect(Collectors.toList());

            // JWT 토큰 생성
            String token = tokenProvider.createToken(auth.getName(), roles);

            return ResponseEntity.ok(
                    new TokenResponse(token, "Bearer", tokenProvider.getValidityInMilliseconds(token))
            );
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 로그인 요청 DTO
     */
    public static record LoginRequest(
            String username,
            String password
    ) {}

    /**
     * 토큰 응답 DTO
     */
    public static record TokenResponse(
            String token,
            String type,
            long expiresIn
    ) {}
}
