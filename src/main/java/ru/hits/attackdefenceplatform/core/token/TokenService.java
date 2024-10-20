package ru.hits.attackdefenceplatform.core.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenUtils jwtTokenUtils;


}
