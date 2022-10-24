package de.x1c1b.attoly.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.x1c1b.attoly.api.security.ajax.AjaxAuthenticationProcessingFilterConfigurer;
import de.x1c1b.attoly.api.security.token.AccessToken;
import de.x1c1b.attoly.api.security.token.TokenProvider;
import de.x1c1b.attoly.api.security.token.auth.AccessTokenAuthenticationProvider;
import de.x1c1b.attoly.api.security.token.filter.AccessTokenAuthenticationFilterConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenProvider<AccessToken> accessTokenProvider;


    @Bean
    PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
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
                .requestMatcher(new AntPathRequestMatcher("/v1/auth/token", HttpMethod.POST.name()))
                .authenticationFailureHandler(authenticationFailureHandler)
                .authenticationSuccessHandler(authenticationSuccessHandler)
                .usernameField("email")
                .passwordField("password")
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
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }

    protected void csrf(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
    }

    protected void cors(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));

            return configuration;
        });
    }

    protected void authenticateRequests(HttpSecurity httpSecurity) throws Exception {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

        AccessTokenAuthenticationProvider accessTokenAuthenticationProvider = new AccessTokenAuthenticationProvider();
        accessTokenAuthenticationProvider.setUserDetailsService(userDetailsService);
        accessTokenAuthenticationProvider.setAccessTokenProvider(accessTokenProvider);

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(accessTokenAuthenticationProvider);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        httpSecurity.authenticationManager(authenticationManager);
    }

    protected void authorizeRequests(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/v1/users")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/v1/auth/token")
                .permitAll()
                .anyRequest()
                .authenticated();
    }
}
