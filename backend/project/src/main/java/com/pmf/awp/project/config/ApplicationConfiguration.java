package com.pmf.awp.project.config;

import java.util.Optional;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pmf.awp.project.model.User;
import com.pmf.awp.project.repository.UserRepository;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@EnableJpaAuditing
public class ApplicationConfiguration {
    @Autowired
    private UserRepository userRepository;

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("[Auth] User not found"));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE")
                        .allowedHeaders("Authorization", "Content-Type");
            }
        };
    }

    @Bean
    AuditorAware<User> auditorAware() {
        return new AuditorAware<User>() {
            @Override
            public Optional<User> getCurrentAuditor() {
                return Optional.ofNullable(SecurityContextHolder.getContext())
                        .map(SecurityContext::getAuthentication)
                        .filter(Authentication::isAuthenticated)
                        .map(Authentication::getPrincipal)
                        .map(User.class::cast);
            }
        };
    }

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer")));
    }

    @Bean
    GroupedOpenApi publicGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.pmf.awp.project.controller")
                .pathsToMatch("/auth/**", "/images/**")
                .build();
    }

    @Bean
    GroupedOpenApi privateGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("private")
                .packagesToScan("com.pmf.awp.project.controller")
                .pathsToMatch("/boards/**", "/users/**")
                .build();
    }
}