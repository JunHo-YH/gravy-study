package kr.gravy.gravystudy.auction.service;

import kr.gravy.gravystudy.auction.dto.AuctionRegistrationDto;
import kr.gravy.gravystudy.auction.entity.Auction;
import kr.gravy.gravystudy.auction.mapper.AuctionMapper;
import kr.gravy.gravystudy.auth.entity.User;
import kr.gravy.gravystudy.common.exception.GravyException;
import kr.gravy.gravystudy.common.exception.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionMapper auctionMapper;

    @Transactional
    public AuctionRegistrationDto.Response registerAuction(final User user, final AuctionRegistrationDto.Request request) {
        LocalDateTime now = LocalDateTime.now();
        validateAuctionTime(request.auctionStartTime(), request.auctionEndTime());

        Auction auction = Auction.createForRegistration(
                user.getId(),
                request.title(),
                request.description(),
                request.category(),
                request.startingPrice(),
                request.minBidIncrement(),
                request.auctionStartTime(),
                request.auctionEndTime(),
                now
        );

        auctionMapper.insertAuction(auction);

        return new AuctionRegistrationDto.Response(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getCategory(),
                auction.getStartingPrice(),
                auction.getMinBidIncrement(),
                auction.getAuctionStartTime(),
                auction.getAuctionEndTime(),
                auction.getCreatedAt()
        );
    }

    private void validateAuctionTime(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new GravyException(Status.INVALID_AUCTION_START_TIME);
        }

        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new GravyException(Status.INVALID_AUCTION_END_TIME);
        }
    }
}
