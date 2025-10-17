package kr.gravy.gravystudy.auction.dto;

import kr.gravy.gravystudy.auction.entity.Auction;
import kr.gravy.gravystudy.auction.model.AuctionStatus;
import kr.gravy.gravystudy.auction.model.Category;

import java.time.LocalDateTime;
import java.util.List;

public class AuctionListDto {

    /**
     * 경매 목록 조회 응답 DTO
     *
     * @param serverTime 서버 현재 시간 (클라이언트 시간 보정용)
     * @param auctions   경매 목록
     * @param totalCount 전체 경매 개수
     * @param page       현재 페이지
     * @param size       페이지당 개수
     */
    public record Response(
            LocalDateTime serverTime,
            List<AuctionItem> auctions,
            Long totalCount,
            Integer page,
            Integer size
    ) {
    }

    /**
     * 경매 개별 항목 DTO
     *
     * @param id               경매 ID
     * @param title            경매 제목
     * @param category         카테고리
     * @param status           경매 상태 (서버에서 계산)
     * @param currentPrice     현재가 (지금은 시작가와 동일)
     * @param auctionStartTime 경매 시작 시간
     * @param auctionEndTime   경매 종료 시간
     * @param thumbnailUrl     대표 이미지 URL
     */
    public record AuctionItem(
            Long id,
            String title,
            Category category,
            AuctionStatus status,
            Long currentPrice,
            LocalDateTime auctionStartTime,
            LocalDateTime auctionEndTime,
            String thumbnailUrl
    ) {
        public static AuctionItem from(Auction auction, String thumbnailUrl) {
            return new AuctionItem(
                    auction.getId(),
                    auction.getTitle(),
                    auction.getCategory(),
                    AuctionStatus.from(auction.getAuctionStartTime(), auction.getAuctionEndTime()),
                    auction.getStartingPrice(),
                    auction.getAuctionStartTime(),
                    auction.getAuctionEndTime(),
                    thumbnailUrl
            );
        }
    }
}