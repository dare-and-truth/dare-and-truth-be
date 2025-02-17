package PNV.DareAndTruth.service;

import static PNV.DareAndTruth.exception.ErrorCode.BADGE_NOT_FOUND;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.request.badge.CreateBadgeRequest;
import PNV.DareAndTruth.dto.request.badge.UpdateBadgeRequest;
import PNV.DareAndTruth.entity.Badge;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.BadgeMapper;
import PNV.DareAndTruth.repository.BadgeRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BadgeService {
    BadgeRepository badgeRepository;
    BadgeMapper badgeMapper;

    public void createBadge(CreateBadgeRequest request) {
        if (badgeRepository.existsByTitleAndIsDeletedFalse(request.getTitle())) {
            throw new AppException(ErrorCode.BADGE_TITLE_EXISTS, HttpStatus.BAD_REQUEST);
        }

        if (request.getEndDay() != null && !request.getStartDay().isBefore(request.getEndDay())) {
            throw new AppException(ErrorCode.END_DATE_MUST_BE_AFTER_START_DATE, HttpStatus.BAD_REQUEST);
        }

        Badge badge = badgeMapper.createBadgeRequestToBadge(request);
        badgeRepository.save(badge);
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAllByIsDeletedFalse();
    }

    public Badge getBadgeById(UUID id) {
        return badgeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(BADGE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public void updateBadge(UUID id, UpdateBadgeRequest request) {
        Badge badge = badgeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BADGE_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (request.getTitle() != null
                && !badge.getTitle().equals(request.getTitle())
                && badgeRepository.existsByTitleAndIsDeletedFalse(request.getTitle())) {
            throw new AppException(ErrorCode.BADGE_TITLE_EXISTS, HttpStatus.BAD_REQUEST);
        }

        if (request.getStartDay() != null
                && request.getEndDay() != null
                && !request.getStartDay().isBefore(request.getEndDay())) {
            throw new AppException(ErrorCode.END_DATE_MUST_BE_AFTER_START_DATE, HttpStatus.BAD_REQUEST);
        }

        if (request.getTitle() != null) badge.setTitle(request.getTitle());
        if (request.getImage() != null) badge.setImage(request.getImage());
        if (request.getDescription() != null) badge.setDescription(request.getDescription());
        if (request.getBadgeCriteria() != null) badge.setBadgeCriteria(request.getBadgeCriteria());
        if (request.getPoints() != null) badge.setPoints(request.getPoints());
        if (request.getStartDay() != null) badge.setStartDay(request.getStartDay());
        if (request.getEndDay() != null) badge.setEndDay(request.getEndDay());
        if (request.getIsActive() != null) badge.setIsActive(request.getIsActive());

        badgeRepository.save(badge);
    }

    public void deleteBadge(UUID id) {
        Badge badge = badgeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BADGE_NOT_FOUND, HttpStatus.NOT_FOUND));
        badge.setIsDeleted(true);
        badgeRepository.save(badge);
    }
}
