package PNV.DareAndTruth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Score;
import PNV.DareAndTruth.repository.ScoreRepository;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.post.PostSummaryProjection;
import PNV.DareAndTruth.dto.request.post.CreatePostRequest;
import PNV.DareAndTruth.entity.Post;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
import PNV.DareAndTruth.repository.PostRepository;
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
        checkAndAwardChallengeCompletionPoints(post, existingUser.get());
    }

    // Phương thức kiểm tra và cộng điểm
    private void checkAndAwardChallengeCompletionPoints(Post post, User user) {
        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // Tìm Challenge có hashtag tương ứng và thời gian chứa ngày hiện tại
        Optional<Challenge> challengeOpt = challengeRepository.findByHashtagAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsDeletedFalse(
                post.getHashtag(), today, today);

        if (challengeOpt.isPresent()) {
            Challenge challenge = challengeOpt.get();

            // Kiểm tra xem đây có phải lần tạo Post đầu tiên trong ngày của user cho Challenge này không
            long postCount = scoreRepository.countPostsByUserAndHashtagAndDate(
                    user.getId(), challenge.getHashtag(), today, today.plusDays(1));

            // Nếu chưa có Post nào trong ngày và chưa nhận điểm cho challenge này trong ngày
            if (postCount == 0 && !scoreRepository.existsByUserIdAndChallengeIdAndScoreTypeAndCreatedAtDate(
                    user.getId(), challenge.getId(), 2, today)) {

                // Cộng 10 điểm cho việc hoàn thành thử thách hàng ngày (scoreType = 2)
                Score score = Score.builder()
                        .user(user)
                        .scoreReceived(10) // Theo tiêu chí: posted: 10
                        .scoreType(2) // Daily challenge completion
                        .createdAt(LocalDateTime.now())
                        .challenge(challenge)
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
}
