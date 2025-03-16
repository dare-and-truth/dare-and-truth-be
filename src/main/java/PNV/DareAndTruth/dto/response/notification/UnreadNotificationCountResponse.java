package PNV.DareAndTruth.dto.response.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UnreadNotificationCountResponse {
    long totalUnreadNotificationCount;
}
