package PNV.DareAndTruth.configuration;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.security.Key;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}