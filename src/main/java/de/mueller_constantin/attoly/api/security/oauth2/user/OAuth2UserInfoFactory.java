package de.mueller_constantin.attoly.api.security.oauth2.user;

import de.mueller_constantin.attoly.api.domain.model.IdentityProvider;
import de.mueller_constantin.attoly.api.security.oauth2.OAuth2AuthenticationProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OAuth2UserInfoFactory {

    private final RestTemplate restTemplate;

    @Autowired
    public OAuth2UserInfoFactory(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OAuth2UserInfo getOAuth2UserInfo(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        final String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equalsIgnoreCase(IdentityProvider.GITHUB.toString())) {
            GitHubOAuth2UserInfo oAuth2UserInfo = new GitHubOAuth2UserInfo(oAuth2User.getAttributes());

            if (null == oAuth2UserInfo.getEmail()) {
                /*
                 * GitHub offers the possibility to hide e-mail addresses in the public profile. This also deletes the
                 * OAuth2 "email" claim. In this case, the e-mail address must be obtained manually.
                 */

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", String.format("Bearer %s", oAuth2UserRequest.getAccessToken().getTokenValue()));

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                ParameterizedTypeReference<List<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {
                };

                try {
                    ResponseEntity<List<Map<String, Object>>> emailResponse = restTemplate.exchange("https://api.github.com/user/emails",
                            HttpMethod.GET, requestEntity, responseType);

                    String email = (String) emailResponse.getBody().stream().filter(e -> (Boolean) e.get("primary")).findFirst().get().get("email");
                    oAuth2UserInfo.setEmail(email);
                } catch (Exception exc) {
                    throw new OAuth2AuthenticationProcessingException("The email could not be obtained from the identity provider");
                }
            }

            return oAuth2UserInfo;
        } else {
            throw new OAuth2AuthenticationProcessingException("Identity provider with identifier '" + registrationId + "' is not supported");
        }
    }
}
