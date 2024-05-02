package com.makowski.bookshelves.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.makowski.bookshelves.security.filter.AuthenticationFilter;
import com.makowski.bookshelves.security.filter.ExceptionHandlerFilter;
import com.makowski.bookshelves.security.filter.JWTAuthorizationFilter;
import com.makowski.bookshelves.security.manager.CustomAuthenticationManager;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
      
    private CustomAuthenticationManager customAuthenticationManager;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
        authenticationFilter.setFilterProcessesUrl("/authenticate");
        http
            .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests((authorize) -> authorize  
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, SecurityConstants.REGISTER_PATH)).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
            .addFilter(authenticationFilter)
            .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
