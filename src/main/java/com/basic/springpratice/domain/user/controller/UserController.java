package com.basic.springpratice.domain.user.controller;

import com.basic.springpratice.domain.user.dto.UserDto;
import com.basic.springpratice.domain.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 회원가입 엔드포인트
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @RequestBody @Valid RegisterRequest request) {
        UserDto userDto = userService.register(request.username(), request.password());
        return ResponseEntity.status(201).body(userDto);
    }

    /**
     * 회원가입 요청 DTO
     */
    public static record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}
}
