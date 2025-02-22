package PNV.DareAndTruth.service;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.request.like.LikeRequest;
import PNV.DareAndTruth.dto.request.like.UnlikeRequest;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Like;
import PNV.DareAndTruth.entity.Post;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
import PNV.DareAndTruth.repository.LikeRepository;
import PNV.DareAndTruth.repository.PostRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeService {
    LikeRepository likeRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    ChallengeRepository challengeRepository;

    public void likeFeed(LikeRequest request, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        UUID feedId;

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        if (likeRepository.existsByUserIdAndFeedId(user.get().getId(), UUID.fromString(request.getFeedId()))) {
            throw new AppException(ErrorCode.LIKE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        if (request.isChallenge()) {
            Optional<Challenge> challenge = challengeRepository.findById(UUID.fromString(request.getFeedId()));
            if (challenge.isEmpty()) {
                throw new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else feedId = challenge.get().getId();
        } else {
            Optional<Post> post = postRepository.findById(UUID.fromString(request.getFeedId()));
            if (post.isEmpty()) {
                throw new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else feedId = post.get().getId();
        }

        Like like = Like.builder().user(user.get()).feedId(feedId).build();

        likeRepository.save(like);
    }

    @Transactional
    public void unlikeFeed(UnlikeRequest request, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Optional<Like> existingLike =
                likeRepository.findByUserIdAndFeedId(user.get().getId(), UUID.fromString(request.getFeedId()));

        likeRepository.delete(
                existingLike.orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_FOUND, HttpStatus.BAD_REQUEST)));
    }
}
