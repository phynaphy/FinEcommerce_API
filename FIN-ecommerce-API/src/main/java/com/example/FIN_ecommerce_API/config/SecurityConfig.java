//package com.example.FIN_ecommerce_API.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                // Enable HTTP Basic authentication to authenticate credentials on initial login requests
//                .httpBasic(Customizer.withDefaults())
//                .authorizeHttpRequests(auth -> auth
//                        // 1. CORS Preflight
//                        .requestMatchers("/api/v1/**").permitAll()
//                        .requestMatchers("/api/v1/categories/**").permitAll()
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//                        // 2. Public Static Assets & Public Auth Routes
//                        .requestMatchers("/uploads/**", "/error", "/api/v1/auth/**").permitAll()
//
//                        // ADDED: Public GET access for viewing products without token
//                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
//
//                        // 3. Secured API Endpoints (Requires authentication for @PreAuthorize to function)
//                        .requestMatchers("/api/v1/web/**", "/api/v1/**").authenticated()
//
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}

package com.example.FIN_ecommerce_API.config;

import com.example.FIN_ecommerce_API.utilities.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Disable httpBasic and formLogin to prevent Spring from expecting Basic auth headers
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. CORS Preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Public Static Assets & Auth Endpoints (/api/v1/mobile/auth/**)
                        .requestMatchers("/uploads/**", "/error", Constant.MAIN_PATH + "/auth/**").permitAll()
                        .requestMatchers(Constant.WEB_PATH + "/**").permitAll()

                        // 3. Public GET access for viewing categories & products without token
                        .requestMatchers(HttpMethod.GET, Constant.MAIN_PATH + "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, Constant.MAIN_PATH + "/products/**").permitAll()

                        // 4. Secured API Endpoints
                        .requestMatchers("/api/v1/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}