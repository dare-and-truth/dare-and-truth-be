package PNV.DareAndTruth.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1001, "An unexpected error has occurred"),
    EMAIL_INVALID(1002, "Wrong email format"),
    EMAIL_EXISTS(1003, "Email already exists"),
    USERNAME_INVALID(1004, "Username must be at least 3 characters long"),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters long"),
    USER_NOT_FOUND(1006, "User does not find"),
    USER_ID_INVALID(1007, "User ID must be a UUID"),
    EMAIL_REQUIRED(1008, "Email is required"),
    USERNAME_REQUIRED(1009, "Username is required"),
    PASSWORD_REQUIRED(1010, "Password is required"),
    BADGE_NOT_FOUND(1015, "Badge not found"),
    BADGE_TITLE_REQUIRED(1016,"Title is required"),
    BADGE_IMAGE_REQUIRED(1017,"Image is required"),
    BADGE_DECS_REQUIRED(1018,"Description is required"),
    BADGE_REQUIRED_COUNT_REQUIRED(1019,"Required count is required"),
    BADGE_POINT_REQUIRED(1020,"Point is required"),
    BADGE_START_DAY_REQUIRED(1021,"Start day is required"),
    BADGE_TITLE_EXISTS(1022, "Badge title already exists"),
    ;

    int errorCode;
    String message;

    ErrorCode(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
