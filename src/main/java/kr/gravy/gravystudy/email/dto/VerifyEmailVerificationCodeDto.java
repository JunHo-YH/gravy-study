package kr.gravy.gravystudy.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class VerifyEmailVerificationCodeDto {

    public record Request(@Email String email,
                          @NotBlank String verificationCode) {
    }

    public record Response(UUID verificationPublicId) {
    }

}
