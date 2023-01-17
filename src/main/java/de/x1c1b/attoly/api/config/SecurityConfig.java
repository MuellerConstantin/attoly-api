package de.x1c1b.attoly.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.x1c1b.attoly.api.security.ajax.AjaxAuthenticationProcessingFilterConfigurer;
import de.x1c1b.attoly.api.security.error.MvcDelegatingAccessDeniedHandler;
import de.x1c1b.attoly.api.security.error.MvcDelegatingAuthenticationEntryPoint;
import de.x1c1b.attoly.api.security.error.MvcDelegatingAuthenticationFailureHandler;
import de.x1c1b.attoly.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import de.x1c1b.attoly.api.security.oauth2.OAuth2AuthenticationFailureHandler;
import de.x1c1b.attoly.api.security.oauth2.OAuth2AuthenticationSuccessHandler;
import de.x1c1b.attoly.api.security.token.AccessToken;
import de.x1c1b.attoly.api.security.token.RefreshToken;
import de.x1c1b.attoly.api.security.token.TokenAuthenticationSuccessHandler;
import de.x1c1b.attoly.api.security.token.TokenProvider;
import de.x1c1b.attoly.api.security.token.auth.AccessTokenAuthenticationProvider;
import de.x1c1b.attoly.api.security.token.auth.RefreshTokenAuthenticationProvider;
import de.x1c1b.attoly.api.security.token.filter.AccessTokenAuthenticationFilterConfigurer;
import de.x1c1b.attoly.api.security.token.filter.RefreshTokenAuthenticationProcessingFilterConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private MvcDelegatingAuthenticationEntryPoint mvcDelegatingAuthenticationEntryPoint;

    @Autowired
    private MvcDelegatingAccessDeniedHandler mvcDelegatingAccessDeniedHandler;

    @Autowired
    private MvcDelegatingAuthenticationFailureHandler mvcDelegatingAuthenticationFailureHandler;

    @Autowired
    private TokenAuthenticationSuccessHandler tokenAuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DefaultOAuth2UserService oAuth2UserService;

    @Autowired
    private TokenProvider<AccessToken> accessTokenProvider;

    @Autowired
    private TokenProvider<RefreshToken> refreshTokenProvider;


    @Bean
    PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        sessionCreationPolicy(httpSecurity);
        exceptionHandling(httpSecurity);
        csrf(httpSecurity);
        cors(httpSecurity);
        authenticateRequests(httpSecurity);
        authorizeRequests(httpSecurity);

        httpSecurity.apply(new AjaxAuthenticationProcessingFilterConfigurer())
                .requestMatcher(new AntPathRequestMatcher("/api/v1/auth/token", HttpMethod.POST.name()))
                .authenticationFailureHandler(mvcDelegatingAuthenticationFailureHandler)
                .authenticationSuccessHandler(tokenAuthenticationSuccessHandler)
                .usernameField("email")
                .passwordField("password")
                .objectMapper(objectMapper)
                .and()
                .apply(new RefreshTokenAuthenticationProcessingFilterConfigurer())
                .requestMatcher(new AntPathRequestMatcher("/api/v1/auth/refresh", HttpMethod.POST.name()))
                .authenticationFailureHandler(mvcDelegatingAuthenticationFailureHandler)
                .authenticationSuccessHandler(tokenAuthenticationSuccessHandler)
                .objectMapper(objectMapper)
                .and()
                .apply(new AccessTokenAuthenticationFilterConfigurer());

        return httpSecurity.build();
    }

    protected void sessionCreationPolicy(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    protected void exceptionHandling(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.exceptionHandling()
                .authenticationEntryPoint(mvcDelegatingAuthenticationEntryPoint)
                .accessDeniedHandler(mvcDelegatingAccessDeniedHandler);
    }

    protected void csrf(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
    }

    protected void cors(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedHeaders(List.of("*"));

            return configuration;
        });
    }

    protected void authenticateRequests(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.oauth2Login()
                .userInfoEndpoint().userService(oAuth2UserService)
                .and()
                .authorizationEndpoint()
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .successHandler(oAuth2AuthenticationSuccessHandler);

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

        AccessTokenAuthenticationProvider accessTokenAuthenticationProvider = new AccessTokenAuthenticationProvider();
        accessTokenAuthenticationProvider.setUserDetailsService(userDetailsService);
        accessTokenAuthenticationProvider.setAccessTokenProvider(accessTokenProvider);

        RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider = new RefreshTokenAuthenticationProvider();
        refreshTokenAuthenticationProvider.setUserDetailsService(userDetailsService);
        refreshTokenAuthenticationProvider.setRefreshTokenProvider(refreshTokenProvider);

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(accessTokenAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(refreshTokenAuthenticationProvider);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        httpSecurity.authenticationManager(authenticationManager);
    }

    protected void authorizeRequests(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/error")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/actuator/**")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/site/**")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/user/verify", "/api/v1/user/reset")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/user/verify", "/api/v1/user/reset")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/auth/token", "/api/v1/auth/refresh")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/shortcuts")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/shortcuts/*")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/shortcuts/*/reports")
                .permitAll()
                .anyRequest()
                .authenticated();
    }
}
