package de.x1c1b.attoly.api.web.v1.error;

import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import de.x1c1b.attoly.api.web.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static de.x1c1b.attoly.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@RestController
@RequestMapping("/error")
public class RestErrorController implements ErrorController {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    public RestErrorController(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @GetMapping
    public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultiValueMap<String, String> params = RestErrorController.toMultiMap(request.getParameterMap());
        Optional<String> optionalTargetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (optionalTargetUrl.isPresent() && RestErrorController.isAuthorizationResponseError(params)) {
            // OAuth2 login aware error handling endpoint
            String targetUrl = UriComponentsBuilder.fromUriString(optionalTargetUrl.get())
                    .queryParam("error", params.getFirst("error"))
                    .queryParam("error_description", params.getFirst("error_description"))
                    .build().toUriString();

            clearAuthenticationAttributes(request, response);
            response.sendRedirect(targetUrl);
        } else {
            throw new EntityNotFoundException();
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    protected static MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());

        map.forEach((key, values) -> {
            if (values.length > 0) {
                for (String value : values) {
                    params.add(key, value);
                }
            }
        });

        return params;
    }

    protected static boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {
        return StringUtils.hasText(request.getFirst("error")) && StringUtils.hasText(request.getFirst("state"));
    }
}
