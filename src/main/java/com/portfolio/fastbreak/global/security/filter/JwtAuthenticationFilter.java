package com.portfolio.fastbreak.global.security.filter;

import com.portfolio.fastbreak.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더 JWT 토큰 추출
        String token = parseBearerToken(request);

        // 2. 토큰이 존재하고, 유효성 검사를 통과한다면
        if (token != null && jwtUtil.validateToken(token)) {
            // 3. 토큰 안에서 회원 이메일 추출
            String email = jwtUtil.extractEmail(token);

            // 4. 이메일로 DB에서 실제 회원 정보 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 5. security에 인증 객체 생성 후 전달
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. securityContext에 인증 정보 저장 후 로그인된 유저로 인식
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. 다음 필터 또는 컨트롤러로 요청 넘기기
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
