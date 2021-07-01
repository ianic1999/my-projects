package com.example.demo.util.oauth2;

import com.example.demo.dto.JwtDTO;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessAuthenticationHandler implements AuthenticationSuccessHandler {
    private final UserService userService;

    private static final String REDIRECT_URL = "https://portal.oklyx.com/auth/";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        try {
            JwtDTO jwt = userService.authenticateSocial(oauthUser);
            httpServletResponse.sendRedirect(REDIRECT_URL + "social?accessToken=" + jwt.getAccessToken() + "&refreshToken=" + jwt.getRefreshToken());
        } catch (UserException e) {
            httpServletResponse.sendRedirect(REDIRECT_URL + "login?message=" + e.getMessage());
        }
    }
}
