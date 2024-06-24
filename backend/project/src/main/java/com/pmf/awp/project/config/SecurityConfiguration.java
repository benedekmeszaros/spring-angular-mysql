package com.pmf.awp.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf
                                .disable())
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                .requestMatchers("/swagger-ui/**").permitAll()
                                                .requestMatchers("/auth/refresh").authenticated()
                                                .requestMatchers("/auth/**").permitAll()
                                                .requestMatchers("/users/me").authenticated()
                                                .requestMatchers("/users/**").permitAll()
                                                .requestMatchers("/images/**").permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(management -> management
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
