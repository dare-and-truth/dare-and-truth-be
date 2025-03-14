package PNV.DareAndTruth.dto.response.post;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartDateEndDateOfPostResponse {
    String startDate;
    String endDate;
}
