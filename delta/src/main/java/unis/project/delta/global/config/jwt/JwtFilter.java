package unis.project.delta.global.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. HTTP 요청 헤더에서 JWT 토큰 낚아채기
        String token = resolveToken(request);

        // 2. 토큰이 존재하고, 유효한 토큰인지 도구(Provider)를 통해 검사
        if (StringUtils.hasText(token) && jwtTokenProvider.validateAccessToken(token)) {

            // 3. 토큰이 진짜라면 내부에서 userId 추출
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            // 4. 기본 권한(ROLE_USER) 부여
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

            // 5. 추출한 userId와 권한 정보를 스프링 시큐리티의 메모리에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 다음 필터나 컨트롤러로 요청 통과
        filterChain.doFilter(request, response);
    }

    // 헤더에서 "Bearer " 글자를 떼어내고 실제 토큰 문자열만 추출하는 헬퍼 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 찐 토큰만 반환
        }
        return null;
    }
}