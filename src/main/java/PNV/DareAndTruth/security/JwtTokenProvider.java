package PNV.DareAndTruth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenProvider {

    SecretKey secretKey;
    static Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();
    long accessTokenValidity = 1000 * 60 * 60;  // 1 hour
    long refreshTokenValidity = 1000L * 60 * 60 * 24 * 30;  // 30 days

    public JwtTokenProvider(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String createToken(String email, String role, boolean isRefreshToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", isRefreshToken ? "refresh" : "access");

        if (!isRefreshToken) {
            claims.put("role", role);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (isRefreshToken ? refreshTokenValidity : accessTokenValidity)))
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
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
