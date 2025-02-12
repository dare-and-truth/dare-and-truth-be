package PNV.DareAndTruth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import PNV.DareAndTruth.dto.request.badge.CreateBadgeRequest;
import PNV.DareAndTruth.entity.Badge;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.BadgeMapper;
import PNV.DareAndTruth.repository.BadgeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BadgeController {
    BadgeRepository badgeRepository;
    BadgeMapper badgeMapper;

    public void createBadge(CreateBadgeRequest request) {
        if (badgeRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.BADGE_TITLE_EXISTS, HttpStatus.BAD_REQUEST);
        }

        Badge badge = badgeMapper.createBadgeRequestToBadge(request);
        badgeRepository.save(badge);
    }
}
