package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.request.challenge.CreateChallengeRequest;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
import PNV.DareAndTruth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChallengeService {
    ChallengeRepository challengeRepository;
    UserRepository userRepository;

    @Transactional
    public void createChallenge(CreateChallengeRequest request) {
        Optional<User> exitingUser = userRepository.findById(UUID.fromString(request.getUserId()));
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new AppException(ErrorCode.END_DATE_MUST_BE_AFTER_START_DATE, HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(request.getIsRequiredOnTime() && request.getStartDate().equals(request.getEndDate())) &&
                request.getEndTime().isBefore(request.getStartTime())) {
            throw new AppException(ErrorCode.END_TIME_MUST_BE_AFTER_START_TIME, HttpStatus.BAD_REQUEST);
        }

        Challenge challenge = Challenge.builder()
                .user(exitingUser.get())
                .hashtag(request.getHashtag())
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isRequiredOnTime(request.getIsRequiredOnTime())
                .isActive(true)
                .isDeleted(false)
                .build();

        challengeRepository.save(challenge);
    }
}
