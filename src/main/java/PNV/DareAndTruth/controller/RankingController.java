package PNV.DareAndTruth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.dto.response.ranking.UserRankingResponse;
import PNV.DareAndTruth.service.RankingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RankingController {
    RankingService rankingService;

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<AppApiResponse<List<UserRankingResponse>>> getRankingOfChallenge(
            @PathVariable String challengeId) {
        List<UserRankingResponse> rankings = rankingService.getRankingOfChallenge(challengeId);
        return ResponseEntity.ok(AppApiResponse.<List<UserRankingResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Ranking retrieved successfully")
                .data(rankings)
                .build());
    }

    @GetMapping()
    public ResponseEntity<AppApiResponse<List<UserRankingResponse>>> getRankingOfServer() {
        List<UserRankingResponse> rankings = rankingService.getRankingOfServer();
        return ResponseEntity.ok(AppApiResponse.<List<UserRankingResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Server Ranking retrieved successfully")
                .data(rankings)
                .build());
    }
}
