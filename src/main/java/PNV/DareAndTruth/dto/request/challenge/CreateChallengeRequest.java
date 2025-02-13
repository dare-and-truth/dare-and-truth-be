package PNV.DareAndTruth.dto.request.challenge;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChallengeRequest {
    @NotBlank(message = "USER_ID_REQUIRED")
    private String userId;

    @NotBlank(message = "HASHTAG_REQUIRED")
    private String hashtag;

    @NotBlank(message = "CONTENT_REQUIRED")
    private String content;

    @NotBlank(message = "MEDIA_URL_REQUIRED")
    private String mediaUrl;

    @NotNull(message = "START_DATE_REQUIRED")
    @FutureOrPresent(message = "START_DATE_FUTURE_OR_PRESENT")
    private LocalDate startDate;

    @NotNull(message = "END_DATE_REQUIRED")
    @Future(message = "END_DATE_FUTURE")
    private LocalDate endDate;
}
