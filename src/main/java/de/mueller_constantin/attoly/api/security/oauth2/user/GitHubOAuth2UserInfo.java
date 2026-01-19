package de.mueller_constantin.attoly.api.security.oauth2.user;

import java.util.HashMap;
import java.util.Map;

public class GitHubOAuth2UserInfo extends OAuth2UserInfo {

    public GitHubOAuth2UserInfo(Map<String, Object> attributes) {
        super(new HashMap<>(attributes));
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }

    public void setEmail(String email) {
        attributes.put("email", email);
    }
}
