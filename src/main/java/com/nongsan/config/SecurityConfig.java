package com.nongsan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").permitAll() // 🔥 để controller tự check
                        .anyRequest().permitAll()
                )

                // 🔥 TẮT LOGIN CỦA SPRING SECURITY
                .formLogin(form -> form.disable())

                .logout(logout -> logout.disable());

        return http.build();
    }
}