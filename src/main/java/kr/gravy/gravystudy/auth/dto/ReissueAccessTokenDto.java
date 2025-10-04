package kr.gravy.gravystudy.auth.dto;

public class ReissueAccessTokenDto {

    public record Response(String accessToken, String refreshToken) {
    }
}
