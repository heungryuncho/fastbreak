package com.portfolio.fastbreak.global.config;

import com.portfolio.fastbreak.global.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (JWT 사용 시 필수, 토큰 자체가 CSRF 방어 역할)
                .csrf(csrf -> csrf.disable())

                // 2. 세션 사용 안 함 (JWT는 Stateless 방식)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. API 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입 / 로그인은 누구나 접근 가능)
                        .requestMatchers("/api/v1/members/signup", "/api/v1/members/login").permitAll()

                        // Swagger 문서도 누구나 접근 가능
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 경기 목록 / 좌석 조회는 누구나 가능
                        .requestMatchers("/api/v1/games/**").permitAll()

                        // 관리자 전용
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 웹소켓 연결 경로는 누구나
                        .requestMatchers("/ws-reservation/**").permitAll()

                        // 그 외 모든 요청은 JWT 인증 필수
                        .anyRequest().authenticated())

                // 4. JWT 필터를 시큐리티 기본 필터 앞에 끼워넣기
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}
