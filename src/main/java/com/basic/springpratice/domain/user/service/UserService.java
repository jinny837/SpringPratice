package com.basic.springpratice.domain.user.service;

import com.basic.springpratice.domain.user.entity.User;
import com.basic.springpratice.domain.user.repository.UserRepository;
import com.basic.springpratice.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(String username, String password) {
        // 중복 사용자명 확인
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(password);

        // 기본 ROLE_USER 할당
        User user = User.builder().username(username).password(encodedPassword).roles(List.of("ROLE_USER")).enabled(true).build();
        User saved = userRepository.save(user);

        // UserDto로 매핑하여 반환
        return new UserDto(saved.getUsername(), "", saved.getRoles(), saved.isEnabled());
    }
}
