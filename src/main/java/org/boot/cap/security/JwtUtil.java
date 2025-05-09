package org.boot.cap.security;

import static org.boot.cap.security.JwtConstant.ACCESS_TOKEN_EXPIRE_TIME;
import static org.boot.cap.security.JwtConstant.REFRESH_TOKEN_EXPIRE_TIME;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.boot.cap.entity.BlacklistedToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.boot.cap.repository.TokenRepository;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private TokenRepository tokenRepository;

    public final Key key;

    // application.properties 에서 secret 값 가져와서 key 에 저장
    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // User 정보를 가지고 AccessToken 만 생성하는 메서드
    public String generateAccessToken(String userId) {
        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    // User 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public JwtDTO generateToken(String userId) {
        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (tokenRepository.existsByTokenValue(token)) {
            return false; // 블랙리스트 처리된 토큰이면 거부
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    // 토큰을 파싱하여 Claims 정보를 가져오는 메서드
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // JWT 토큰에서 userId 문자열 추출하는 메서드
    public String extractUserId(String token) {
        log.info("jwtUtil.extractUserId(): " + token);
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = parseClaims(token);
        return claims.getSubject(); // userId 리턴
    }

    // 로그아웃 시 토큰 블랙리스트에 추가
    @Transactional
    public void blacklistToken(String accessToken, String refreshToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        // 로그로 확인
        System.out.println("블랙리스트에 저장된 accessToken: " + accessToken);
        System.out.println("블랙리스트에 저장된 refreshToken: " + refreshToken);

        // 블랙리스트에 추가
        tokenRepository.save(new BlacklistedToken(accessToken));
        tokenRepository.save(new BlacklistedToken(refreshToken));

        // 즉시 flush()하여 DB에 반영
        tokenRepository.flush();

        // 블랙리스트 저장된 토큰 검증
        Optional<BlacklistedToken> accessTokenInDb = tokenRepository.findByTokenValue(accessToken);
        System.out.println("DB에 저장된 accessToken이 존재하는지 확인: " + accessTokenInDb.isPresent());

        Optional<BlacklistedToken> refreshTokenInDb = tokenRepository.findByTokenValue(refreshToken);
        System.out.println("DB에 저장된 refreshToken이 존재하는지 확인: " + refreshTokenInDb.isPresent());
    }

}
