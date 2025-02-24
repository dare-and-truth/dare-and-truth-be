package PNV.DareAndTruth.service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdAndUsernameProjection;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchService {
    final ChallengeRepository challengeRepository;
    final UserRepository userRepository;
    UUID existingUserId;

    // Find the user by email and set their ID.
    // If the user is not found, throw an error.
    private void setUserIdFromEmail(String userEmail) {
        Optional<User> exitingUser = userRepository.findByEmail(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        this.existingUserId = exitingUser.get().getId();
    }

    // Remove accents from a string.
    private String removeDiacritics(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // Convert a keyword into a list of words, remove accents, and remove empty words.
    private List<String> getNormalizedWords(String keyword) {
        return Arrays.stream(keyword.toLowerCase().split("\\s+"))
                .map(this::removeDiacritics)
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
    }

    // Combine two lists and remove duplicates based on the ID.
    private <T> List<T> combineResults(
            List<T> firstResults, List<T> secondResults, java.util.function.Function<T, UUID> getIdFunction) {
        Set<UUID> seenIds = new HashSet<>();
        List<T> combinedResults = new ArrayList<>();

        for (T result : firstResults) {
            if (seenIds.add(getIdFunction.apply(result))) {
                combinedResults.add(result);
            }
        }

        for (T result : secondResults) {
            if (seenIds.add(getIdFunction.apply(result))) {
                combinedResults.add(result);
            }
        }

        return combinedResults;
    }

    // Search for users based on a keyword.
    // First, find exact matches. Then, search for partial matches word by word.
    public List<UserWithIdAndUsernameProjection> searchUsers(String keyword, String userEmail) {
        setUserIdFromEmail(userEmail);
        String normalizedKeyword = removeDiacritics(keyword.toLowerCase()).replaceAll("\\s+", "");

        List<UserWithIdAndUsernameProjection> firstSearchResults =
                userRepository.searchUsersByNormalizedKeyword(normalizedKeyword, existingUserId);

        List<UserWithIdAndUsernameProjection> secondSearchResults = getNormalizedWords(keyword).stream()
                .flatMap(word -> userRepository
                        .searchUsersBySingleWord(
                                word,
                                existingUserId,
                                firstSearchResults.stream()
                                        .map(UserWithIdAndUsernameProjection::getId)
                                        .collect(Collectors.toList()))
                        .stream())
                .collect(Collectors.toList());

        return combineResults(firstSearchResults, secondSearchResults, UserWithIdAndUsernameProjection::getId);
    }

    // Search for challenges based on a keyword.
    // First, find exact matches. Then, search for partial matches word by word.
    public List<ChallengeSummaryProjection> searchChallenges(String keyword) {
        String normalizedKeyword = removeDiacritics(keyword.toLowerCase()).replaceAll("\\s+", "");

        List<ChallengeSummaryProjection> firstSearchResults =
                challengeRepository.searchChallengesByNormalizedKeyword(normalizedKeyword, new ArrayList<>());

        List<ChallengeSummaryProjection> secondSearchResults = getNormalizedWords(keyword).stream()
                .flatMap(word -> challengeRepository.searchChallengesBySingleWord(word).stream())
                .collect(Collectors.toList());

        return combineResults(firstSearchResults, secondSearchResults, ChallengeSummaryProjection::getId);
    }
}
