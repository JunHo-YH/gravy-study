package kr.gravy.gravystudy.auction.mapper;

import kr.gravy.gravystudy.auction.entity.Auction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuctionMapper {

    void insertAuction(Auction auction);

    /**
     * 경매 목록을 페이지네이션하여 조회합니다.
     *
     * @param offset 시작 위치
     * @param limit  조회 개수
     * @return 경매 목록
     */
    List<Auction> findAuctionsWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    long countTotalAuctions();
}
