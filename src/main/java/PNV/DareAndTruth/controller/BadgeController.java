package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.entity.Badge;
import PNV.DareAndTruth.service.BadgeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BadgeController {
    BadgeService badgeService;

    @GetMapping
    public ResponseEntity<AppApiResponse<List<Badge>>> getAllBadges() {
        List<Badge> allBadges = badgeService.getAllBadges();
        return ResponseEntity.ok(AppApiResponse.<List<Badge>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Badges retrieved successfully")
                .data(allBadges)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppApiResponse<Badge>> getBadgeById(@PathVariable String id){
        Badge badge = badgeService.getBadgeById(UUID.fromString(id));
        return ResponseEntity.ok(
                AppApiResponse.<Badge>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Badge retrieved successfully")
                        .data(badge)
                        .build()
        );
    }

}
