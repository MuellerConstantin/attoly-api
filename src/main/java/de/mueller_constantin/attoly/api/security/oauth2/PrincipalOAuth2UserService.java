package de.mueller_constantin.attoly.api.security.oauth2;

import de.mueller_constantin.attoly.api.domain.model.IdentityProvider;
import de.mueller_constantin.attoly.api.domain.model.RoleName;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.repository.RoleRepository;
import de.mueller_constantin.attoly.api.repository.UserRepository;
import de.mueller_constantin.attoly.api.security.EmailNotFoundException;
import de.mueller_constantin.attoly.api.security.Principal;
import de.mueller_constantin.attoly.api.security.oauth2.user.OAuth2UserInfo;
import de.mueller_constantin.attoly.api.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Primary
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    @Autowired
    public PrincipalOAuth2UserService(UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      OAuth2UserInfoFactory oAuth2UserInfoFactory) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.oAuth2UserInfoFactory = oAuth2UserInfoFactory;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return loadUserByOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException exc) {
            throw exc;
        } catch (Exception exc) {
            throw new InternalAuthenticationServiceException(exc.getMessage(), exc.getCause());
        }
    }

    public Principal loadUserByOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) throws EmailNotFoundException {
        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest, oAuth2User);

        if (null == oAuth2UserInfo.getEmail() || oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationProcessingException("The email could not be obtained from the identity provider");
        }

        User user;
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (userOptional.isEmpty()) {
            user = User.builder()
                    .email(oAuth2UserInfo.getEmail())
                    .emailVerified(true)
                    .roles(Set.of(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow()))
                    .identityProvider(IdentityProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()))
                    .identityProviderId(oAuth2UserInfo.getId())
                    .build();

            user = userRepository.save(user);
        } else {
            user = userOptional.get();
        }

        return new Principal(user, oAuth2User.getAttributes());
    }
}
