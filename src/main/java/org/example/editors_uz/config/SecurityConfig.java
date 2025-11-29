package org.example.editors_uz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CookieAuthenticationFilter cookieAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/auth", "/logout", "/products","/payment/submit")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth",
                                "/file/**",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/",
                                "/index",
                                "/courses",
                                "/templates",
                                "/course/buy/**",
                                "/payment/submit",
                                "/basket",
                                "/logout"
                        ).authenticated()

                        .requestMatchers("/add", "/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(cookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/auth"))
                );

        return http.build();
    }
}