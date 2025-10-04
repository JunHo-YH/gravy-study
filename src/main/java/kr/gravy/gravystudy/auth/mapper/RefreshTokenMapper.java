package kr.gravy.gravystudy.auth.mapper;

import kr.gravy.gravystudy.auth.entity.RefreshToken;
import kr.gravy.gravystudy.auth.model.RefreshTokenStatus;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenMapper {
    void insertRefreshToken(RefreshToken refreshToken);

    void revokeActiveTokenByUserId(@Param("userId") Long userId,
                                   @Param("updatedAt") LocalDateTime updatedAt);

    Optional<RefreshToken> getRefreshTokenByStatus(@Param("refreshToken") String refreshToken,
                                                   @Param("refreshTokenStatus") RefreshTokenStatus refreshTokenStatus);
}
