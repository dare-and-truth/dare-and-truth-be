package PNV.DareAndTruth.dto.request.like;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;

@Getter
@Setter
public class UnlikeRequest {
    @NotNull(message = "POST_ID_REQUIRED")
    @UUID(message = "POST_ID_INVALID")
    private String feedId;
}
