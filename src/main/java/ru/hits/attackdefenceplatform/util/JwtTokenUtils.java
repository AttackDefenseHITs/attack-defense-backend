package ru.hits.attackdefenceplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.lifetime}")
    private Duration jwtAccessLifetime;

    @Value("${jwt.refresh.lifetime}")
    private Duration jwtRefreshLifetime;

    public String generateAccessToken(UserDto user) {
        return generateToken(user, jwtAccessLifetime);
    }

    public String generateRefreshToken(UserDto user) {
        return generateToken(user, jwtRefreshLifetime);
    }

    private String generateToken(UserDto user, Duration lifetime) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());
        UUID tokenId = UUID.randomUUID();

        return Jwts.builder()
                .setSubject(user.login())
                .claim("userId", user.id().toString())
                .claim("role", user.role())
                .setId(tokenId.toString())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        String userId = getAllClaimsFromToken(token).get("userId", String.class);
        return UUID.fromString(userId);
    }

    public String getTokenId(String token) {
        var claims = getAllClaimsFromToken(token);
        return claims.getId();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
