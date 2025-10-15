package kr.gravy.gravystudy.auction.service;

import kr.gravy.gravystudy.auction.dto.AuctionRegistrationDto;
import kr.gravy.gravystudy.auction.entity.Auction;
import kr.gravy.gravystudy.auction.entity.AuctionImage;
import kr.gravy.gravystudy.auction.mapper.AuctionImageMapper;
import kr.gravy.gravystudy.auction.mapper.AuctionMapper;
import kr.gravy.gravystudy.auth.entity.User;
import kr.gravy.gravystudy.infrastructure.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionMapper auctionMapper;
    private final AuctionImageMapper auctionImageMapper;
    private final S3Service s3Service;

    @Transactional
    public AuctionRegistrationDto.Response registerAuction(
            final User user,
            final AuctionRegistrationDto.Request request,
            final List<MultipartFile> images) {

        LocalDateTime now = LocalDateTime.now();
        validateImagesIfPresent(images);

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

        List<String> imageUrls = uploadAndSaveImages(auction.getId(), images, now);

        return new AuctionRegistrationDto.Response(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getCategory(),
                auction.getStartingPrice(),
                auction.getMinBidIncrement(),
                auction.getAuctionStartTime(),
                auction.getAuctionEndTime(),
                auction.getCreatedAt(),
                imageUrls
        );
    }

    private void validateImagesIfPresent(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        s3Service.validatePossibleUploadImages(images);
    }

    private List<String> uploadAndSaveImages(Long auctionId, List<MultipartFile> images, LocalDateTime now) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        List<String> uploadedUrls = new ArrayList<>();
        try {
            for (int order = 0; order < images.size(); order++) {
                MultipartFile file = images.get(order);

                // S3 업로드
                String imageUrl = s3Service.uploadFile(file);
                uploadedUrls.add(imageUrl);

                AuctionImage auctionImage = AuctionImage.create(auctionId, imageUrl, order, now);
                auctionImageMapper.insertAuctionImage(auctionImage);
            }
            return uploadedUrls;
        } catch (Exception e) {
            // 업로드 실패 시 이미 업로드된 S3 파일들 삭제
            log.error("이미지 업로드 중 오류 발생 - {} 개 파일 롤백 중", uploadedUrls.size());
            rollbackUploadedImages(uploadedUrls);
            throw e;
        }
    }

    private void rollbackUploadedImages(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            try {
                s3Service.deleteFile(imageUrl);
                log.info("S3 롤백 성공: {}", imageUrl);
            } catch (Exception e) {
                log.error("S3 롤백 실패 (수동 처리 필요): {}", imageUrl, e);
            }
        }
    }
}
