package ru.hits.attackdefenceplatform.configuration;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                UUID userId = jwtTokenUtils.getUserIdFromToken(jwt);
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserEntity user = userRepository.findById(userId)
                            .orElse(null);
                    if (user != null) {
                        List<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                        );
                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                                user, jwt, authorities
                        );
                        SecurityContextHolder.getContext().setAuthentication(token);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.debug("Token is expired");
            } catch (Exception e) {
                log.error("Error processing JWT", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
