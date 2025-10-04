package kr.gravy.gravystudy.auth.service;

import kr.gravy.gravystudy.auth.dto.ReissueAccessTokenDto;
import kr.gravy.gravystudy.auth.dto.UserLoginDto;
import kr.gravy.gravystudy.auth.dto.UserSignUpDto;
import kr.gravy.gravystudy.auth.entity.RefreshToken;
import kr.gravy.gravystudy.auth.entity.User;
import kr.gravy.gravystudy.auth.jwt.JWTUtil;
import kr.gravy.gravystudy.auth.mapper.RefreshTokenMapper;
import kr.gravy.gravystudy.auth.mapper.UserMapper;
import kr.gravy.gravystudy.auth.model.Grade;
import kr.gravy.gravystudy.auth.model.RefreshTokenStatus;
import kr.gravy.gravystudy.common.exception.GravyException;
import kr.gravy.gravystudy.common.exception.Status;
import kr.gravy.gravystudy.common.utils.DateTimeUtil;
import kr.gravy.gravystudy.common.utils.GeneratorUtil;
import kr.gravy.gravystudy.email.entity.EmailVerification;
import kr.gravy.gravystudy.email.mapper.EmailVerificationMapper;
import kr.gravy.gravystudy.email.model.EmailVerificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final EmailVerificationMapper emailVerificationMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JWTUtil jwtUtil;


    @Transactional
    public void signUp(final UserSignUpDto.Request request) {
        EmailVerification emailVerification = emailVerificationMapper.getEmailVerificationByPublicId(request.verificationPublicId())
                .orElseThrow(() -> new GravyException(Status.BAD_REQUEST));
        validateEmailVerifiedStatus(emailVerification.getStatus());
        emailVerificationMapper.updateVerificationStatus(emailVerification.getId(), EmailVerificationStatus.CONSUMED);

        User user = User.createForSignUp(
                GeneratorUtil.generatePublicId(),
                request.nickname(),
                emailVerification.getEmail(),
                passwordEncoder.encode(request.password()),
                Grade.BASIC,
                LocalDateTime.now()
        );

        userMapper.userSignUp(user);
    }

    private void validateEmailVerifiedStatus(final EmailVerificationStatus verificationStatus) {
        if (!Objects.equals(verificationStatus, EmailVerificationStatus.VERIFIED)) {
            throw new GravyException(Status.EMAIL_NOT_VERIFIED);
        }
    }

    @Transactional
    public UserLoginDto.Response userLogin(final UserLoginDto.Request request) {
        User user = userMapper.getUserByEmail(request.email().trim())
                .orElseThrow(() -> new GravyException(Status.USER_NOT_FOUND));

        user.validatePassword(passwordEncoder, request.password());

        UUID userPublicId = user.getPublicId();
        String accessToken = jwtUtil.createAccessToken(userPublicId);
        String refreshToken = jwtUtil.createRefreshToken(userPublicId);

        final LocalDateTime now = LocalDateTime.now();
        Date refreshTokenExpiredDate = jwtUtil.getExpiration(refreshToken);
        LocalDateTime refreshTokenExpiredAt = DateTimeUtil.convertToLocalDateTime(refreshTokenExpiredDate);

        RefreshToken newRefreshToken = RefreshToken.create(
                user.getId(),
                refreshToken,
                RefreshTokenStatus.ACTIVE,
                refreshTokenExpiredAt,
                now,
                now
        );
        refreshTokenMapper.revokeActiveTokenByUserId(user.getId(), now);
        refreshTokenMapper.insertRefreshToken(newRefreshToken);

        return new UserLoginDto.Response(accessToken, refreshToken);
    }

    @Transactional
    public ReissueAccessTokenDto.Response reissueAccessToken(final String requestedRefreshToken) {
        validateRefreshToken(requestedRefreshToken);

        User user = userMapper.getUserByRefreshToken(requestedRefreshToken)
                .orElseThrow(() -> new GravyException(Status.USER_NOT_FOUND));

        final LocalDateTime now = LocalDateTime.now();
        String accessToken = jwtUtil.createAccessToken(user.getPublicId());
        String refreshToken = jwtUtil.createRefreshToken(user.getPublicId());

        Date refreshTokenExpiredDate = jwtUtil.getExpiration(refreshToken);
        LocalDateTime refreshTokenExpiredAt = DateTimeUtil.convertToLocalDateTime(refreshTokenExpiredDate);
        RefreshToken refreshTokenVO = RefreshToken.create(
                user.getId(),
                refreshToken,
                RefreshTokenStatus.ACTIVE,
                refreshTokenExpiredAt,
                now,
                now
        );
        refreshTokenMapper.revokeActiveTokenByUserId(user.getId(), now);
        refreshTokenMapper.insertRefreshToken(refreshTokenVO);

        return new ReissueAccessTokenDto.Response(accessToken, refreshToken);
    }

    private void validateRefreshToken(String requestedRefreshToken) {
        RefreshToken refreshToken =
                refreshTokenMapper.getRefreshTokenByStatus(requestedRefreshToken, RefreshTokenStatus.ACTIVE)
                        .orElseThrow(() -> new GravyException(Status.TOKEN_NOT_FOUND));

        final LocalDateTime now = LocalDateTime.now();
        if (refreshToken.getExpiredAt().isBefore(now)) {
            throw new GravyException(Status.TOKEN_EXPIRED);
        }
    }

    @Transactional
    public void userLogout(final String requestedRefreshToken) {
        // refresh token 유효성 검증
        validateRefreshToken(requestedRefreshToken);

        // 해당 사용자의 모든 활성 refresh token 무효화
        User user = userMapper.getUserByRefreshToken(requestedRefreshToken)
                .orElseThrow(() -> new GravyException(Status.USER_NOT_FOUND));

        // 모든 활성 Refresh Token 무효화 (REVOKED)
        final LocalDateTime now = LocalDateTime.now();
        refreshTokenMapper.revokeActiveTokenByUserId(user.getId(), now);
    }
}
