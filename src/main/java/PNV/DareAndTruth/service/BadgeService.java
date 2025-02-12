package PNV.DareAndTruth.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.request.badge.CreateBadgeRequest;
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
        if (badgeRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.BADGE_TITLE_EXISTS, HttpStatus.BAD_REQUEST);
        }

        Badge badge = badgeMapper.createBadgeRequestToBadge(request);
        badgeRepository.save(badge);
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAllByIsDeletedFalse();
    }

    public Badge getBadgeById(UUID id) {
        return (Badge) badgeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BADGE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public void deleteBadge(UUID id) {
        Badge badge = getBadgeById(id);
        badge.setIsDeleted(true);
        badgeRepository.save(badge);
    }
}
