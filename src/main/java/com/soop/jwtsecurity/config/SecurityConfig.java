
package com.soop.jwtsecurity.config;

import com.soop.jwtsecurity.handler.CustomSuccessHandler;
import com.soop.jwtsecurity.jwt.JWTFilter;
import com.soop.jwtsecurity.jwt.JWTUtil;
import com.soop.jwtsecurity.mapper.UserMapper;
import com.soop.jwtsecurity.service.CustomOAuth2UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;

@Tag(name = "시큐리티 설정", description = "시큐리티 설정 파일 입니다.")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final UserMapper userMapper;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil,UserMapper userMapper) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Operation(summary = "시큐리티 필터 체인", description = "시큐리티 필터체인입니다.")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        http.addFilterBefore(new JWTFilter(jwtUtil,userMapper), UsernamePasswordAuthenticationFilter.class);
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
        );
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Operation(summary = "CORS 설정", description = " CROSS ALLOWED ORIGIN 설정")
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3001","http://localhost:3002"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With", "Access-Token", "Refresh-Token"));
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
