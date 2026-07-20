package unis.project.delta.global.config;

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

        // 🌟 1. 경로별 권한 설정 (인가)
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/auth/**").permitAll() // 카카오 로그인, 토큰 재발급 등은 토큰 없이 통과
                .anyRequest().authenticated() // 그 외의 모든 API(소비, 예산 등)는 반드시 토큰 인증 필요!
        );

        // 🌟 2. 필터 체인에 JwtFilter 끼워넣기
        // UsernamePasswordAuthenticationFilter 가 실행되기 "전"에 우리 JwtFilter가 먼저 실행되도록 설정
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}