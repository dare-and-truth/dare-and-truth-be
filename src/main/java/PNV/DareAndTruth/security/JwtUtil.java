package PNV.DareAndTruth.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    SecretKey secretKey;

    public JwtUtil(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired!", e);
        } catch (MalformedJwtException | SignatureException e) {
            throw new RuntimeException("Invalid token!", e);
        }
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
