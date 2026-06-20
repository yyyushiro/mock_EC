package com.example.springboot.service;

import com.example.springboot.dto.AuthTokens;
import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.RegisterRequest;
import com.example.springboot.exception.InvalidRefreshTokenException;
import com.example.springboot.security.DatabaseUserDetailsService;
import com.example.springboot.security.JwtService;
import com.example.springboot.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final DatabaseUserDetailsService databaseUserDetailsService;
    private final UserService userService;

    /**
     * login() authenticates the user with the given request, then issue access token and refresh token.
     *
     * @param request the login reqeust with email and password.
     * @return access token and refresh token.
     */
    public AuthTokens login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // Issue access token
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        String refreshToken = refreshTokenService.issueRefreshToken(userDetails.getUsername());

        return new AuthTokens(jwt, refreshToken);
    }

    public AuthTokens refresh(String refreshToken) {
        // issue new access token and refresh token.
        String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
        if (newRefreshToken == null) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String username = refreshTokenService.findUsernameFromRedis(newRefreshToken).orElse(null);

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername(username);
        String newJwt = jwtService.generateToken(userDetails);

        return new AuthTokens(newJwt, newRefreshToken);
    }

    // This method is just a wrapper now, but might be useful if you will add other functionalities to the logout.
    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    public AuthTokens register(RegisterRequest request) {

        // save user
        UserDetails userDetails = userService.saveUser(request);

        // issue tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.issueRefreshToken(userDetails.getUsername());

        return new AuthTokens(accessToken, refreshToken);
    }
}
