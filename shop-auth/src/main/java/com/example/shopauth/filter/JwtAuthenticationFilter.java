package com.example.shopauth.filter;

import com.example.shopauth.service.CustomUserDetails;
import com.example.shopauth.service.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String prefix = "Bearer ";
        String jwt = null;

        if (authHeader != null && authHeader.startsWith(prefix)) {
            jwt = authHeader.substring(prefix.length());
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                System.out.println(">>> JWT received: " + jwt);

                String username = jwtService.extractUsername(jwt);
                Long userId = jwtService.extractUserId(jwt); //phải có extractId()
                String email = jwtService.extractEmail(jwt); // cần implement extractEmail()
                List<String> roles = jwtService.extractRoles(jwt);

                System.out.println(">>> Extracted username=" + username + ", userId=" + userId + ", email=" + email + ", roles=" + roles);

                if (username != null && userId != null) {
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> role.startsWith("ROLE_")
                                    ? new SimpleGrantedAuthority(role)
                                    : new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    CustomUserDetails userDetails = new CustomUserDetails(
                            userId,
                            username,
                            email != null ? email : "",
                            "",
                            authorities,
                            true, true, true, true
                    );

                    boolean valid = jwtService.isTokenValid(jwt, userDetails);
                    System.out.println(">>> Token validation result: " + valid);

                    if (valid) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        System.out.println(">>> Authentication set in SecurityContext: " + username
                                + " with roles " + authorities);
                    } else {
                        System.out.println(">>> Token INVALID, skipping authentication.");
                    }
                } else {
                    System.out.println(">>> Username or userId is null, cannot build CustomUserDetails");
                }

            } catch (Exception e) {
                System.out.println(">>> Exception while processing JWT: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (jwt == null) {
            System.out.println(">>> No JWT found in request headers");
        }

        filterChain.doFilter(request, response);
    }
}
