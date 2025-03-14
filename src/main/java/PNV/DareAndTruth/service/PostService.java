package PNV.DareAndTruth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import PNV.DareAndTruth.dto.response.post.StartDateEndDateOfPostResponse;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.post.PostSummaryProjection;
import PNV.DareAndTruth.dto.request.post.CreatePostRequest;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Post;
import PNV.DareAndTruth.entity.Score;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
import PNV.DareAndTruth.repository.PostRepository;
import PNV.DareAndTruth.repository.ScoreRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    UserRepository userRepository;
    ChallengeRepository challengeRepository;
    ScoreRepository scoreRepository;

    @Transactional
    public void createPost(CreatePostRequest request, String userEmail) {
        Optional<User> existingUser = userRepository.findByEmail(userEmail);
        if (existingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Post post = Post.builder()
                .user(existingUser.get())
                .hashtag(request.getHashtag())
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .isActive(true)
                .isDeleted(false)
                .build();

        postRepository.save(post);
        Optional<Challenge> activeChallengeOpt = challengeRepository.findByHashtagAndIsActiveTrue(post.getHashtag());

        if (activeChallengeOpt.isPresent()) {
            Challenge activeChallenge = activeChallengeOpt.get();

            LocalDateTime todayStart =
                    LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

            boolean scoreExists = scoreRepository.existsByUserIdAndScoreTypeAndChallengeIdAndCreatedAtAfter(
                    existingUser.get().getId(), 2, activeChallenge.getId(), todayStart);

            if (!scoreExists) {
                Score score = Score.builder()
                        .user(existingUser.get())
                        .scoreReceived(10)
                        .scoreType(2)
                        .challenge(activeChallenge)
                        .createdAt(LocalDateTime.now())
                        .build();

                scoreRepository.save(score);
            }
        }
    }

    public Set<PostSummaryProjection> getPosts() {
        return postRepository.findAllByIsDeletedFalse();
    }

    public PostSummaryProjection getPostById(String id) {
        UUID postId;
        try {
            postId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.POST_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        return postRepository
                .findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public StartDateEndDateOfPostResponse getStartDateAndEndDateByHashtagAndCreatedAt(String hashtag, String createdAtStr) {
        // Parse the createdAt parameter as a LocalDateTime, then extract the date part
        LocalDate createdAt;
        try {
            LocalDateTime ldt = LocalDateTime.parse(createdAtStr);
            createdAt = ldt.toLocalDate();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_DATE_FORMAT, HttpStatus.BAD_REQUEST);
        }

        // Retrieve the challenge where the createdAt falls between startDate and endDate
        Optional<Challenge> challengeOpt = challengeRepository.findActiveChallengeByHashtagAndCreatedAt(hashtag, createdAt);
        if (challengeOpt.isEmpty()) {
            throw new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Challenge challenge = challengeOpt.get();
        // Build and return the response DTO
        return new StartDateEndDateOfPostResponse(
                challenge.getStartDate().toString(),
                challenge.getEndDate().toString()
        );
    }
}
