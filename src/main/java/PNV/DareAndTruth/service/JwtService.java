package PNV.DareAndTruth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtService {

    SecretKey secretKey;
    static Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    @Value("${app.jwt.access-token-expiration-ms}")
    long accessTokenValidity;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    long refreshTokenValidity;

    @Value("${app.jwt.secret}")
    String secret;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createToken(String email, String role, boolean isRefreshToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", isRefreshToken ? "refresh" : "access");

        if (!isRefreshToken) {
            claims.put("role", role);
        }

        long expirationTime = isRefreshToken ? refreshTokenValidity : accessTokenValidity;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public static boolean isTokenInvalid(String token) {
        return invalidatedTokens.contains(token);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.TOKEN_ALREADY_INVALID, HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException | SecurityException e) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.BAD_REQUEST);
        }
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        return authorizationHeader.substring(7); // Cắt "Bearer " để lấy token thực sự
    }
}
