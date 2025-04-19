package com.basic.springpratice.domain.user.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 사용자 정보를 표현하는 DTO
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles;


    @Column(nullable = false)
    private boolean enabled;
}
