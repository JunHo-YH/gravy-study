package kr.gravy.gravystudy.auction.controller;

import jakarta.validation.Valid;
import kr.gravy.gravystudy.auction.dto.AuctionListDto;
import kr.gravy.gravystudy.auction.dto.AuctionRegistrationDto;
import kr.gravy.gravystudy.auction.service.AuctionService;
import kr.gravy.gravystudy.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping(value = "/api/v1/auctions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuctionRegistrationDto.Response> registerAuction(
            @AuthenticationPrincipal User user,
            @Valid @ModelAttribute AuctionRegistrationDto.Request request,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.registerAuction(user, request, images));
    }

    @GetMapping("/api/v1/auctions")
    public ResponseEntity<AuctionListDto.Response> getAuctions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(auctionService.getAuctions(page, size));
    }
}
