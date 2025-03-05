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
        // 1️⃣ Tìm challenge theo hashtag và trạng thái active
        Optional<Challenge> activeChallengeOpt = challengeRepository.findByHashtagAndIsActiveTrue(post.getHashtag());

        if (activeChallengeOpt.isPresent()) {
            Challenge activeChallenge = activeChallengeOpt.get();

            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

            // 2️⃣ Kiểm tra xem đã nhận điểm cho challenge này chưa (trong ngày)
            boolean scoreExists = scoreRepository.existsByUserIdAndScoreTypeAndChallengeIdAndCreatedAtAfter(
                    existingUser.get().getId(),
                    2,
                    activeChallenge.getId(),
                    todayStart
            );

            // 3️⃣ Nếu chưa có điểm cho challenge này, thì thêm điểm
            if (!scoreExists) {
                Score score = Score.builder()
                        .user(existingUser.get())
                        .scoreReceived(10)  // Điểm cho việc đăng bài
                        .scoreType(2)       // Loại điểm cho đăng bài
                        .challenge(activeChallenge) // Lưu Challenge ID
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
}
