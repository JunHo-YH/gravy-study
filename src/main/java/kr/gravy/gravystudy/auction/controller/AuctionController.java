package kr.gravy.gravystudy.auction.controller;

import jakarta.validation.Valid;
import kr.gravy.gravystudy.auction.dto.AuctionRegistrationDto;
import kr.gravy.gravystudy.auction.service.AuctionService;
import kr.gravy.gravystudy.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping("/api/v1/auctions")
    public ResponseEntity<AuctionRegistrationDto.Response> registerAuction(@AuthenticationPrincipal User user,
                                                                           @Valid @RequestBody AuctionRegistrationDto.Request request) {
        AuctionRegistrationDto.Response response = auctionService.registerAuction(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
