package com.example.springboot.service;

import com.example.springboot.dto.AuthTokens;
import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.RegisterRequest;
import com.example.springboot.security.DatabaseUserDetailsService;
import com.example.springboot.security.JwtService;
import com.example.springboot.security.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    JwtService jwtService;

    @Mock
    DatabaseUserDetailsService databaseUserDetailsService;

    @Mock
    UserService userService;

    @Mock
    UserDetails userDetails;

    @Mock
    Authentication authentication;

    @Test
    void login_shouldReturnTokens_whenCredentialsAreValid() {
        // まずテストしたいメソッドの引数を確定させる。
        LoginRequest reqeust = new LoginRequest("user@example.com", "password");

        // その後、テストしたいメソッドの中にある関数に返して欲しい値を決定する。
        // serviceのことを意識して戻り値を返させるのではなく、あくまでもservice内のコードの流れにそいつつも、
        // 各関数が機能していた場合に本来返されるべき値を独立して返すように設定するべきである。
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("access-token");
        when(userDetails.getUsername()).thenReturn("user@example.com");

        when(refreshTokenService.issueRefreshToken(userDetails.getUsername())).thenReturn("refresh-token");

        AuthTokens result = authService.login(reqeust);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void login_shouldPropagateException_whenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("user@example.com", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad Credentials"));

        // authService.login(request)をそのまま渡すのではなくて、ラムダ式にすることで、
        // assertThatThrownByにいつその関数を実行させるかを決めさせている。
        // これによって、引数にとった時点で例外が発生して終了することなく比較できる。
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void refresh_shouldReturnNewTokens_whenRefreshTokenIsValid() {
        when(refreshTokenService.rotateRefreshToken("old-refresh-token"))
                .thenReturn("new-refresh-token");
        when(refreshTokenService.findUsernameFromRedis("new-refresh-token"))
                .thenReturn(Optional.of("user@example.com"));
        when(databaseUserDetailsService.loadUserByUsername("user@example.com"))
                .thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("new-access-token");

        AuthTokens result = authService.refresh("old-refresh-token");

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void logout_shouldDeleteRefreshToken() {
        authService.logout("refresh-token");

        verify(refreshTokenService).deleteRefreshToken("refresh-token");
    }

    @Test
    void register_shouldSaveUserAndReturnTokens() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password", "CUSTOMER");

        when(userService.saveUser(request)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("access-token");

        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(refreshTokenService.issueRefreshToken(userDetails.getUsername())).thenReturn("refresh-token");

        AuthTokens result = authService.register(request);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }
}
