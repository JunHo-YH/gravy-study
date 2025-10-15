package kr.gravy.gravystudy.auction.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuctionImage {

    private Long id;
    private Long auctionId;
    private String imageUrl;
    private int displayOrder;
    private LocalDateTime createdAt;

    private AuctionImage(Long auctionId, String imageUrl, int displayOrder, LocalDateTime createdAt) {
        this.auctionId = auctionId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }

    public static AuctionImage create(Long auctionId, String imageUrl, int displayOrder, LocalDateTime createdAt) {
        return new AuctionImage(auctionId, imageUrl, displayOrder, createdAt);
    }
}
