package kr.gravy.gravystudy.auction.mapper;

import kr.gravy.gravystudy.auction.dto.AuctionThumbnail;
import kr.gravy.gravystudy.auction.entity.AuctionImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuctionImageMapper {

    void insertAuctionImage(AuctionImage auctionImage);

    /**
     * 여러 경매의 첫 번째 이미지를 한 번에 조회합니다.
     *
     * @param auctionIdList 경매 ID 목록
     * @return 경매 썸네일 목록
     */
    List<AuctionThumbnail> findFirstImagesByAuctionIdList(@Param("auctionIdList") List<Long> auctionIdList);
}
