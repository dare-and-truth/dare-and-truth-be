package PNV.DareAndTruth.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1001, "An unexpected error has occurred"),
    EMAIL_INVALID(1002, "Please enter a valid email address."),
    EMAIL_EXISTS(1003, "Email already exists"),
    USERNAME_INVALID(1004, "Username must be at least 3 characters long"),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters long"),
    USER_NOT_FOUND(1006, "User does not find"),
    USER_ID_INVALID(1007, "User ID must be a UUID"),
    EMAIL_REQUIRED(1008, "Email is required"),
    USERNAME_REQUIRED(1009, "Username is required"),
    PASSWORD_REQUIRED(1010, "Password is required"),
    EMAIL_NOT_FOUND(1011, "Email is not found. Please sign up."),
    PASSWORD_INCORRECT(1012, "Password is incorrect"),
    INVALID_REFRESH_TOKEN(1013, "Invalid refresh token"),
    REFRESH_TOKEN_REQUIRED(1014, "Refresh token is required"),
    TOKEN_ALREADY_INVALID(1015, "Token has been disabled!"),
    BADGE_NOT_FOUND(1016, "Badge not found"),
    BADGE_TITLE_REQUIRED(1017, "Title is required"),
    BADGE_IMAGE_REQUIRED(1018, "Image is required"),
    BADGE_DECS_REQUIRED(1019, "Description is required"),
    BADGE_CRITERIA_REQUIRED(1020, "Badge Criteria is required"),
    BADGE_POINT_REQUIRED(1021, "Point is required"),
    BADGE_START_DAY_REQUIRED(1022, "Start day is required"),
    BADGE_TITLE_EXISTS(1023, "Badge title already exists"),
    INVALID_BADGE_DATE_RANGE(1024, "Start day should be less than end day"),
    BADGE_POINT_INVALID(1024, "Points must be at least 0"),
    BADGE_CRITERIA_INVALID(1024, "Badge criteria must be at least 0"),
    BADGE_IS_ACTIVE_REQUIRED(1024, "Is active is required"),
    ;

    int code;
    String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
