package de.x1c1b.attoly.api.security.oauth2;

import de.x1c1b.attoly.api.security.token.AccessToken;
import de.x1c1b.attoly.api.security.token.RefreshToken;
import de.x1c1b.attoly.api.security.token.TokenProvider;
import de.x1c1b.attoly.api.web.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static de.x1c1b.attoly.api.security.oauth2.StatelessOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider<AccessToken> accessTokenTokenProvider;
    private final TokenProvider<RefreshToken> refreshTokenTokenProvider;
    private final StatelessOAuth2AuthorizationRequestRepository statelessOAuth2AuthorizationRequestRepository;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(TokenProvider<AccessToken> accessTokenTokenProvider,
                                              TokenProvider<RefreshToken> refreshTokenTokenProvider,
                                              StatelessOAuth2AuthorizationRequestRepository statelessOAuth2AuthorizationRequestRepository) {
        this.accessTokenTokenProvider = accessTokenTokenProvider;
        this.refreshTokenTokenProvider = refreshTokenTokenProvider;
        this.statelessOAuth2AuthorizationRequestRepository = statelessOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());

        AccessToken accessToken = accessTokenTokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenTokenProvider.generateToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("access_token", accessToken.getRawToken())
                .queryParam("refresh_token", refreshToken.getRawToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        statelessOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
