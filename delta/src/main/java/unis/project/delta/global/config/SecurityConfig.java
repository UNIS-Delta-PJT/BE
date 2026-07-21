package unis.project.delta.global.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import unis.project.delta.global.config.jwt.JwtFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // cors 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5174",
                "http://localhost:5173", // 프론트 로컬 테스트 주소
                "http://localhost:3000", // 프론트 서브 포트 대비용
                "https://singing-moisture-voters-don.trycloudflare.com", // 백엔드 배포 주소
                "https://delta-lovat-six.vercel.app")); // 프론트 배포 주소
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));
        http.sessionManagement(
                sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 1. 시큐리티 예외 발생 시 403이 아닌 명세서대로 401을 내보내도록 설정
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("🚨 [시큐리티 인증 실패]: " + authException.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\",\"status\":401,\"error\":\"Unauthorized\",\"code\":\"INVALID_ACCESS_TOKEN\",\"message\":\"유효하지 않거나 만료된 Access Token입니다.\"}");
                })
        );

        // 2. 경로별 권한 설정
        // SecurityConfig.java 내부
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/auth/**", "/api/health", "/health", "/error").permitAll() // 🌟 헬스체크 & 에러 경로 토큰 없이 허용!
                .requestMatchers(org.springframework.web.cors.CorsUtils::isPreFlightRequest).permitAll() // Preflight (OPTIONS) 허용
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}