package com.basic.springpratice.common.config;

import com.basic.springpratice.common.security.JwtAuthenticationFilter;
import com.basic.springpratice.common.security.JwtTokenProvider;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Value("${spring.security.oauth2.resourceserver.jwt.secret}")
    private String base64Secret;

    /**
     * PasswordEncoder 빈: BCrypt를 권장
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 빈: Spring Security 6부터 직접 빈 등록
     * UserDetailsService와 PasswordEncoder를 이용한 DaoAuthenticationProvider 설정
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder);
        // 만약 여러 Provider를 쓴다면 ProviderManager에 주입
        //return new ProviderManager(/* your AuthenticationProvider beans */);
        return new ProviderManager(List.of(daoProvider));
    }

    /**
     * SecurityFilterChain: HTTP 보안 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API이므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // CORS 설정 (필요하다면 세부 조정)
                .cors(Customizer.withDefaults())

                // 세션은 사용하지 않음 → 무상태
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .securityMatcher("/api/**")         // 보호할 URL 패턴
                // 인증·인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // Swagger / Actuator / public API 등 열람 허용
                        .requestMatchers(
                                HttpMethod.GET, "/docs/**", "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/actuator/**", "/actuator/info").authenticated()

                        // 인증(로그인) 엔드포인트는 누구나
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        // 나머지 API는 인증 필요
                        .anyRequest().authenticated()
                )

//                .authorizeHttpRequests(auth -> auth
//
//                        .anyRequest().hasAuthority("SCOPE_api.read")
//                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 기본 폼 로그인/ HTTP Basic 비활성화
                .httpBasic(basic -> basic.disable())
                .formLogin(login -> login.disable());

        return http.build();
    }



    /** JWT의 scope, roles 클레임을 GrantedAuthority로 변환 */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthorityPrefix("SCOPE_");
        scopesConverter.setAuthoritiesClaimName("scope");
        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(scopesConverter);
        return authConverter;
    }


    /**
     * (선택) CORS 설정 커스터마이징 예시
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("*")); //https://your-front.example.com
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HMACSHA256");

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)   // 기대하는 알고리즘 명시
                .build();

        // (선택) 추가 검증 규칙 설정
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(""));
        return jwtDecoder;
    }

}
