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

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static de.x1c1b.attoly.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider<AccessToken> accessTokenTokenProvider;
    private final TokenProvider<RefreshToken> refreshTokenTokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(TokenProvider<AccessToken> accessTokenTokenProvider,
                                              TokenProvider<RefreshToken> refreshTokenTokenProvider,
                                              HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.accessTokenTokenProvider = accessTokenTokenProvider;
        this.refreshTokenTokenProvider = refreshTokenTokenProvider;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
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

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        AccessToken accessToken = accessTokenTokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenTokenProvider.generateToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("access_token", accessToken.getRawToken())
                .queryParam("refresh_token", refreshToken.getRawToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
