package PNV.DareAndTruth.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import PNV.DareAndTruth.dto.projection.user.UserWithIdProjection;
import PNV.DareAndTruth.dto.response.challenge.ChallengeWithUserAndLikeCountAndCommentCountResponse;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.dto.request.challenge.CreateChallengeRequest;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChallengeService {
    ChallengeRepository challengeRepository;
    UserRepository userRepository;

    @Transactional
    public void createChallenge(CreateChallengeRequest request, String userEmail) {
        Optional<User> exitingUser = userRepository.findByEmail(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new AppException(ErrorCode.END_DATE_MUST_BE_AFTER_START_DATE, HttpStatus.BAD_REQUEST);
        }

        // Validate hashtag in range date from start date to end date
        boolean existsWithOverlappingDates = challengeRepository.existsWithOverlappingDates(request.getHashtag(), request.getStartDate(), request.getEndDate());

        if (existsWithOverlappingDates) {
            throw new AppException(ErrorCode.HASHTAG_ALREADY_EXISTS_IN_DATE_RANGE, HttpStatus.BAD_REQUEST);
        }

        Challenge challenge = Challenge.builder()
                .user(exitingUser.get())
                .hashtag(request.getHashtag())
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .isDeleted(false)
                .build();

        challengeRepository.save(challenge);
    }

    public Set<ChallengeSummaryProjection> getChallenges() {
        return challengeRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
    }

    public ChallengeSummaryProjection getChallengeById(String id) {
        UUID challengeId;
        try {
            challengeId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.CHALLENGE_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        return challengeRepository
                .findByIdAndIsDeletedFalse(challengeId)
                .orElseThrow(() -> new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public List<ChallengeWithUserAndLikeCountAndCommentCountResponse> getChallengesWithLikeCount(String userEmail) {
        Optional<UserWithIdProjection> user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return challengeRepository.findAllChallengesWithLikeCountAndCommentCount(user.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND)).getId());
    }
}
