package PNV.DareAndTruth.service;

import PNV.DareAndTruth.entity.Badge;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.repository.BadgeRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static PNV.DareAndTruth.exception.ErrorCode.BADGE_NOT_FOUND;

@Getter
@Setter
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BadgeService {
    BadgeRepository badgeRepository;

    public List<Badge> getAllBadges() {
        return badgeRepository.findAllByIsDeletedFalse();
    }

    public Badge getBadgeById(UUID id) {
        return badgeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(BADGE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

}
