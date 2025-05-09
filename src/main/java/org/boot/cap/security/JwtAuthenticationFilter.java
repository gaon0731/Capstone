package org.boot.cap.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);
        log.debug("Request URI: {}", request.getRequestURI());
        log.debug("Authorization header: {}", request.getHeader("Authorization"));

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                Authentication authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // AccessToken 이 만료됐을 경우 401 Unauthorized 응답
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access token expired");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    // 요청 헤더에서 Bearer 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰에서 인증 정보 생성
    private Authentication getAuthentication(String token) {
        Claims claims = jwtUtil.parseClaims(token);
        String userId = claims.getSubject();

        UserDetails userDetails = User.withUsername(userId).password("").authorities(Collections.emptyList()).build();
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

}
