package kr.gravy.gravystudy.auction.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum AuctionStatus {
    SCHEDULED("경매 예정"),
    ONGOING("경매 진행중"),
    ENDED("경매 종료");

    private final String description;

    /**
     * 경매 시작/종료 시간을 기준으로 현재 상태를 계산합니다.
     *
     * @param startTime 경매 시작 시간
     * @param endTime   경매 종료 시간
     * @return 계산된 경매 상태
     */
    public static AuctionStatus from(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startTime)) {
            return SCHEDULED;
        }

        if (now.isAfter(endTime)) {
            return ENDED;
        }

        return ONGOING;
    }
}