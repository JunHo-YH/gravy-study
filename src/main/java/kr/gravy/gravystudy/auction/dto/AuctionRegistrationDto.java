package kr.gravy.gravystudy.auction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.gravy.gravystudy.auction.model.Category;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuctionRegistrationDto {

    public record Request(
            @NotBlank(message = "상품 제목은 필수입니다.") String title,
            @NotBlank(message = "상품 설명은 필수입니다.") String description,
            @NotNull(message = "카테고리는 필수입니다.") Category category,
            @NotNull(message = "시작 가격은 필수입니다.") @Positive(message = "시작 가격은 0보다 커야 합니다.") Long startingPrice,
            @NotNull(message = "최소 입찰 단위는 필수입니다.") @Positive(message = "최소 입찰 단위는 0보다 커야 합니다.") Long minBidIncrement,
            @NotNull(message = "경매 시작 시간은 필수입니다.") LocalDateTime auctionStartTime,
            @NotNull(message = "경매 종료 시간은 필수입니다.") LocalDateTime auctionEndTime
    ) {
    }

    public record Response(
            Long id,
            String title,
            String description,
            Category category,
            Long startingPrice,
            Long minBidIncrement,
            LocalDateTime auctionStartTime,
            LocalDateTime auctionEndTime,
            LocalDateTime createdAt
    ) {
    }
}