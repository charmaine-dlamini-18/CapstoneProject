package za.ac.cput.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import za.ac.cput.security.*;
import java.util.List;

/*
 * SecurityConfig.java
 * Configures security and controls access to application endpoints.
 * Author: Sabelo Ceza - 220094489
 * Date: 08/11/2026
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Frontend addresses allowed to access the backend.
    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private List<String> allowedOrigins;

    // Checks if the application should only use HTTPS.
    @Value("${app.security.require-https:false}")
    private boolean requireHttps;

    private final LoginRateLimiter loginRateLimiter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final ObjectMapper objectMapper;

    // Constructor to get the security classes needed by this file.
    public SecurityConfig(LoginRateLimiter loginRateLimiter,
                          RestAuthenticationEntryPoint authenticationEntryPoint,
                          RestAccessDeniedHandler accessDeniedHandler,
                          ObjectMapper objectMapper) {

        this.loginRateLimiter = loginRateLimiter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.objectMapper = objectMapper;
    }

    // Creates the password encoder.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configures how Spring Security checks user login details.
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        // Shows the exact reason when a login fails.
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    // Main security configuration for the application.
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {

        http
                .authenticationProvider(authenticationProvider)

                // Enables CORS and disables CSRF for the stateless API.
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // The application does not use server-side sessions.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Defines which users can access each endpoint.
                .authorizeHttpRequests(auth -> auth

                        // Student endpoints.
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/student/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/student/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/student/resend-verification").permitAll()

                        // Tutor endpoints.
                        .requestMatchers(HttpMethod.POST, "/tutor/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tutor/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tutor/resend-verification").permitAll()

                        // The user must be logged in to view their own account details.
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()

                        // Admin access.
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Students must be able to browse tutors, subjects and
                        // tutor-subject links (read-only) so they can find tutors
                        // and book sessions. These read rules must come before the
                        // broader role rules below.
                        .requestMatchers(HttpMethod.GET, "/tutor/getAll", "/tutor/read/**")
                        .hasAnyRole("ADMIN", "TUTOR", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/subject/getAll", "/subject/read/**")
                        .hasAnyRole("ADMIN", "TUTOR", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/tutorsubject/getAll", "/tutorsubject/read/**")
                        .hasAnyRole("ADMIN", "TUTOR", "STUDENT")

                        // Tutor access.
                        .requestMatchers("/tutor/**").hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/tutorsubject/**").hasAnyRole("ADMIN", "TUTOR")

                        // Subject management is restricted to admins.
                        .requestMatchers("/subject/**").hasRole("ADMIN")

                        // Student access.
                        .requestMatchers("/student/**").hasAnyRole("ADMIN", "STUDENT")

                        // Payment access.
                        .requestMatchers("/payment/**").hasAnyRole("ADMIN", "STUDENT")

                        // Only admins and students can create bookings.
                        .requestMatchers(HttpMethod.POST, "/booking/create")
                        .hasAnyRole("ADMIN", "STUDENT")

                        // Other booking endpoints.
                        .requestMatchers("/booking/**")
                        .hasAnyRole("ADMIN", "STUDENT", "TUTOR")

                        // All remaining endpoints require authentication.
                        .anyRequest().authenticated()
                )

                // Handles authentication and access-denied errors.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                // Uses HTTP Basic authentication.
                .httpBasic(basic ->
                        basic.authenticationEntryPoint(authenticationEntryPoint))

                // Checks login requests before Spring Security processes them.
                .addFilterBefore(
                        new RateLimitFilter(loginRateLimiter, objectMapper),
                        UsernamePasswordAuthenticationFilter.class
                );

        // Requires HTTPS when enabled in application properties.
        if (requireHttps) {
            http.requiresChannel(channel ->
                    channel.anyRequest().requiresSecure());
        }

        return http.build();
    }

    // Configures which frontend addresses can communicate with the backend.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(
                List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
