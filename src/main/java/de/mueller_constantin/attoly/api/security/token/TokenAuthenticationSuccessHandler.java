package de.mueller_constantin.attoly.api.security.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mueller_constantin.attoly.api.security.token.auth.RefreshTokenAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenProvider<AccessToken> accessTokenTokenProvider;
    private final OneTimeTokenProvider<RefreshToken> refreshTokenTokenProvider;

    @Autowired
    public TokenAuthenticationSuccessHandler(ObjectMapper objectMapper,
                                             TokenProvider<AccessToken> accessTokenTokenProvider,
                                             OneTimeTokenProvider<RefreshToken> refreshTokenTokenProvider) {
        this.objectMapper = objectMapper;
        this.accessTokenTokenProvider = accessTokenTokenProvider;
        this.refreshTokenTokenProvider = refreshTokenTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        AccessToken accessToken = accessTokenTokenProvider.generateToken(authentication);
        RefreshToken refreshToken = null;

        if (authentication instanceof RefreshTokenAuthenticationToken) {
            String oldRefreshToken = (String) authentication.getCredentials();
            refreshToken = refreshTokenTokenProvider.exchange(oldRefreshToken);
        } else {
            refreshToken = refreshTokenTokenProvider.generateToken(authentication);
        }

        Map<String, Object> token = new HashMap<>();
        token.put("type", "Bearer");
        token.put("principal", accessToken.getPrincipal());
        token.put("accessToken", accessToken.getRawToken());
        token.put("accessExpiresIn", accessToken.getExpiresIn());

        assert refreshToken != null;

        token.put("refreshToken", refreshToken.getRawToken());
        token.put("refreshExpiresIn", refreshToken.getExpiresIn());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), token);
    }
}
