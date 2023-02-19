package com.kbsl.kbslserver.auth.fliter;

import com.kbsl.kbslserver.auth.domain.repository.AuthTokenRepository;
import com.kbsl.kbslserver.boot.exception.RestException;
import com.kbsl.kbslserver.boot.util.JwtUtils;
import com.kbsl.kbslserver.user.service.principal.PrincipalUserDetail;
import com.kbsl.kbslserver.user.service.principal.PrincipalUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthJwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final PrincipalUserDetailService userDetailService;
    private final AuthTokenRepository authTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = getAccessToken(request);
            String requestPath = request.getServletPath();

            log.info(requestPath);

            if(accessToken != null && jwtUtils.validateAccessToken(accessToken)) {
                log.info(accessToken);

                if(!authTokenRepository.existsByAccessToken(accessToken)) {
                    throw new RestException(HttpStatus.BAD_REQUEST, "Access Token 이 DB 내 토큰과 일치하지 않습니다. 이전 사용자/로그아웃된 사용자, 혹은 조작된 토큰일 수 있습니다.");
                }

                String username = jwtUtils.getUserNameFromAccessToken(accessToken);

                log.info("username = {}", username);

                PrincipalUserDetail userDetail = (PrincipalUserDetail) userDetailService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch ( StackOverflowError e ) {

            System.err.println("Exception: " + e );
            // Here is the important thing to do
            // when catching StackOverflowError's:
            e.printStackTrace();
            // do some cleanup and destroy the thread or unravel if possible.
        }
        catch (SecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.replace("Bearer ", "");
        }

        return null;
    }
}