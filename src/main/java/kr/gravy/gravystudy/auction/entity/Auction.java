package kr.gravy.gravystudy.auction.entity;

import kr.gravy.gravystudy.auction.model.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Auction {
    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private Category category;
    private Long startingPrice;
    private Long minBidIncrement;
    private LocalDateTime auctionStartTime;
    private LocalDateTime auctionEndTime;
    private LocalDateTime createdAt;

    private Auction(
            Long sellerId,
            String title,
            String description,
            Category category,
            Long startingPrice,
            Long minBidIncrement,
            LocalDateTime auctionStartTime,
            LocalDateTime auctionEndTime,
            LocalDateTime createdAt
    ) {
        this.sellerId = sellerId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.startingPrice = startingPrice;
        this.minBidIncrement = minBidIncrement;
        this.auctionStartTime = auctionStartTime;
        this.auctionEndTime = auctionEndTime;
        this.createdAt = createdAt;
    }

    public static Auction createForRegistration(
            Long sellerId,
            String title,
            String description,
            Category category,
            Long startingPrice,
            Long minBidIncrement,
            LocalDateTime auctionStartTime,
            LocalDateTime auctionEndTime,
            LocalDateTime createdAt
    ) {
        return new Auction(
                sellerId,
                title,
                description,
                category,
                startingPrice,
                minBidIncrement,
                auctionStartTime,
                auctionEndTime,
                createdAt
        );
    }
}