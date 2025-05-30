package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final JwtSessionRepository jwtSessionRepository;

    private final List<AntPathRequestMatcher> protectedMatchers=
            List.of(new AntPathRequestMatcher("/api/**"));

    private final List<AntPathRequestMatcher> excludeMatchers=List.of(
            new AntPathRequestMatcher("/api/auth/login", "POST"),
            new AntPathRequestMatcher("/api/auth/me", "GET"),
            new AntPathRequestMatcher("/api/auth/logout", "POST"),
            new AntPathRequestMatcher("/api/binaryContents/**", "GET"),
            new AntPathRequestMatcher("/api/auth/csrf-token", "GET"),
            new AntPathRequestMatcher("/"),
            new AntPathRequestMatcher("/index.html"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/actuator/**"),
            new AntPathRequestMatcher("/favicon.ico"),
            new AntPathRequestMatcher("/assets/index-*.js"),
            new AntPathRequestMatcher("/assets/index-*.css"),
            new AntPathRequestMatcher("/static/**")
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeMatchers.stream().anyMatch(m->m.matches(request))||
                protectedMatchers.stream().noneMatch(m->m.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!requiresAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if(authHeader==null||!authHeader.startsWith("Bearer ")){
            log.warn("Authorization header is not a valid JWT token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = authHeader.substring(7);
        if(!jwtService.validateToken(token)){
            log.warn("Invalid JWT token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!jwtSessionRepository.existsByAccessToken(token)) {
            log.warn("DB not found access token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserDto userDto = jwtService.extractUserDto(token);
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(userDto.username());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
        log.debug("Authentication principal class: {}",
                authentication.getPrincipal().getClass().getName());
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        if (excludeMatchers.stream().anyMatch(m ->
                m.matches(request))) {
            return false;
        }
        return protectedMatchers.stream().anyMatch(m->
                m.matches(request));
    }
}
