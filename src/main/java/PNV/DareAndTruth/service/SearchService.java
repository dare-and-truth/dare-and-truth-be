package PNV.DareAndTruth.service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.request.FriendDetailProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdAndUsernameProjection;
import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import PNV.DareAndTruth.dto.response.user.UserWithRequestsResponse;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
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
public class SearchService {
    ChallengeRepository challengeRepository;
    UserRepository userRepository;

    // Find the user by email and set their ID.
    // If the user is not found, throw an error.
    private String removeDiacritics(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private UUID getUserIdFromEmail(String userEmail) {
        return userRepository
                .findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public List<GetFeedResponse> searchChallenges(String keyword, String userEmail) {
        UUID userId = getUserIdFromEmail(userEmail);
        String normalizedKeyword = removeDiacritics(keyword.toLowerCase()).replaceAll("\\s+", "");
        List<UUID> excludedIds = new ArrayList<>();

        List<GetFeedResponse> normalizedResults = challengeRepository.searchChallengesByNormalizedKeyword(
                normalizedKeyword,
                userId,
                ChallengeRepository.SPECIAL_CHARACTERS,
                ChallengeRepository.REPLACEMENT_CHARACTERS);

        List<GetFeedResponse> results = normalizedResults.stream()
                .filter(challenge -> !excludedIds.contains(challenge.getId()))
                .collect(Collectors.toList());
        normalizedResults.forEach(challenge -> excludedIds.add(challenge.getId()));

        if (!results.isEmpty()) {
            return results;
        }

        List<String> words = Arrays.stream(keyword.split("\\s+"))
                .map(word -> removeDiacritics(word.toLowerCase()))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());

        List<GetFeedResponse> newResults;
        for (String word : words) {
            List<GetFeedResponse> wordResults = challengeRepository.searchChallengesBySingleWord(
                    word,
                    excludedIds,
                    ChallengeRepository.SPECIAL_CHARACTERS,
                    ChallengeRepository.REPLACEMENT_CHARACTERS);

            newResults = wordResults.stream()
                    .filter(challenge -> !excludedIds.contains(challenge.getId()))
                    .collect(Collectors.toList());

            results.addAll(newResults);
            newResults.forEach(result -> excludedIds.add(result.getId()));
        }

        List<GetFeedResponse> excludingResults = challengeRepository.searchChallengesExcludingIds(
                normalizedKeyword,
                excludedIds,
                ChallengeRepository.SPECIAL_CHARACTERS,
                ChallengeRepository.REPLACEMENT_CHARACTERS);

        newResults = excludingResults.stream()
                .filter(challenge -> !excludedIds.contains(challenge.getId()))
                .collect(Collectors.toList());

        results.addAll(newResults);

        return results.stream().distinct().collect(Collectors.toList());
    }

    public List<UserWithRequestsResponse> searchUsers(String keyword, String userEmail) {
        UUID currentUserId = getUserIdFromEmail(userEmail);
        String normalizedKeyword = removeDiacritics(keyword.toLowerCase()).replaceAll("\\s+", "");
        List<UUID> excludedIds = new ArrayList<>();

        List<UserWithIdAndUsernameProjection> normalizedResults =
                userRepository.searchUsersByNormalizedKeyword(normalizedKeyword, currentUserId);

        List<UserWithRequestsResponse> results = normalizedResults.stream()
                .filter(user -> !excludedIds.contains(user.getId())) // Loại bỏ trùng lặp
                .map(user -> createUserWithRequestsResponse(user, currentUserId))
                .collect(Collectors.toList());
        normalizedResults.forEach(user -> excludedIds.add(user.getId())); // Cập nhật excludedIds

        if (!results.isEmpty()) {
            return results;
        }

        List<String> words = Arrays.stream(keyword.split("\\s+"))
                .map(word -> removeDiacritics(word.toLowerCase()))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());

        for (String word : words) {
            List<UserWithIdAndUsernameProjection> wordResults =
                    userRepository.searchUsersBySingleWord(word, currentUserId, excludedIds);

            List<UserWithRequestsResponse> newResults = wordResults.stream()
                    .filter(user -> !excludedIds.contains(user.getId()))
                    .map(user -> createUserWithRequestsResponse(user, currentUserId))
                    .collect(Collectors.toList());

            results.addAll(newResults);
            newResults.forEach(result -> excludedIds.add(result.getUser().getId()));
        }

        return results.stream().distinct().collect(Collectors.toList());
    }

    private UserWithRequestsResponse createUserWithRequestsResponse(
            UserWithIdAndUsernameProjection user, UUID currentUserId) {
        List<FriendDetailProjection> requests = userRepository.findRequestsByUserId(currentUserId);

        List<FriendDetailProjection> userRequests = requests.stream()
                .filter(r -> r.getUser().getId().equals(user.getId())
                        || r.getFollower().getId().equals(user.getId()))
                .collect(Collectors.toList());

        return new UserWithRequestsResponse(user, userRequests);
    }
}
