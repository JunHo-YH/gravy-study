package kr.gravy.gravystudy.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.gravy.gravystudy.auth.entity.User;
import kr.gravy.gravystudy.configuration.properties.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JWTUtil {

    private final Key key;

    private final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(30);
    private final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);

    public JWTUtil(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
    }

    public String createAccessToken(User user) {
        return createToken(user, ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(User user) {
        return createToken(user, REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(User user, Duration duration) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getPublicId().toString())
                .claim("grade", user.getGrade().name())
                .issuer("Gravy")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(duration)))
                .signWith(key)
                .compact();
    }

    public Date getExpiration(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
