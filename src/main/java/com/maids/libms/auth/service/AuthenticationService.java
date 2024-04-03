package com.maids.libms.auth.service;

import com.maids.libms.auth.model.Token;
import com.maids.libms.auth.repository.TokenRepository;
import com.maids.libms.auth.dto.*;
import com.maids.libms.auth.enums.TokenType;
import com.maids.libms.main.service.EmailService;
import com.maids.libms.main.ResponseDto;
import com.maids.libms.main.exception.CommonExceptions;
import com.maids.libms.patron.Patron;
import com.maids.libms.patron.PatronRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    final TokenRepository tokenRepository;
    final PatronRepository patronRepository;
    final JwtService jwtService;
    final AuthenticationManager authenticationManager;
    final EmailService emailService;

    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<ResponseDto<String>> register(RegisterRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var patron = modelMapper.map(request, Patron.class);
        patron.setActivationKey(generateActivationKey());
        var savedPatron = patronRepository.save(patron);
        emailService.sendVerificationEmail(savedPatron.getEmail(), savedPatron.getActivationKey());
        return ResponseDto.response("Verification code sent successfully");
    }

    @Transactional
    public ResponseEntity<ResponseDto<VerifyResponse>> verify(
            VerifyRequest requestBody,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = getRefreshTokenFromAuthHeader(request);
        Patron patron = getPatronFromRefreshToken(refreshToken);
        if (!Objects.equals(requestBody.getCode(), patron.getActivationKey())) {
            log.info("invalid activation code attempt: session-" + request.getSession()
                    + ", email-" + patron.getEmail());
            throw new CommonExceptions.UnauthorizedException("Invalid activation code");
        }
        patron.setActivated(true);
        patronRepository.save(patron);
        var jwtToken = jwtService.generateToken(patron);
        var newRefreshToken = jwtService.generateRefreshToken(patron);
        savePatronToken(patron, jwtToken);
        var data = VerifyResponse.builder()
                .patron(patron)
                .accessToken(jwtToken)
                .refreshToken(newRefreshToken)
                .build();
        return ResponseDto.response(data);
    }

    @Transactional
    public ResponseEntity<ResponseDto<LoginResponse>> login(LoginRequest request) {
        var patron = patronRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CommonExceptions.ResourceNotFoundException("email does not exist"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(patron);
        var refreshToken = jwtService.generateRefreshToken(patron);
        revokeAllPatronTokens(patron);
        savePatronToken(patron, jwtToken);
        var data = LoginResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseDto.response(data);
    }

    void savePatronToken(Patron patron, String jwtToken) {
        var token = Token.builder()
                .patron(patron)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    String generateActivationKey() {
        return (int) Math.floor(Math.random() * (99998 - 10000 + 1) + 10000) + "";
    }

    void revokeAllPatronTokens(Patron patron) {
        log.info("revoked all tokens for patron " + patron.getEmail());
        var validPatronTokens = tokenRepository.findAllValidTokenByPatron(patron.getId());
        if (validPatronTokens.isEmpty())
            return;
        validPatronTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validPatronTokens);
    }

    Patron getPatronFromRefreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            String message = userEmail + "does not exist";
            var patron = this.patronRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new CommonExceptions.ResourceNotFoundException(message));
            if (jwtService.isTokenValid(refreshToken, patron)) {
                return patron;
            }
        }
        throw new CommonExceptions.UnauthorizedException("Invalid token");
    }

    String getRefreshTokenFromAuthHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CommonExceptions.UnauthorizedException("invalid authorization header");
        }
        refreshToken = authHeader.substring(7);
        return refreshToken;
    }

    public ResponseEntity<ResponseDto<LoginResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String refreshToken = getRefreshTokenFromAuthHeader(request);
        Patron patron = getPatronFromRefreshToken(refreshToken);
        var accessToken = jwtService.generateToken(patron);
        revokeAllPatronTokens(patron);
        savePatronToken(patron, accessToken);
        var authResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseDto.response(authResponse);
    }
}
