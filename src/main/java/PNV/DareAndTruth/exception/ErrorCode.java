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
    EMAIL_NOT_FOUND(1011, "Email is not found. Please sign up."),
    PASSWORD_INCORRECT(1012, "Password is incorrect"),
    INVALID_REFRESH_TOKEN(1013, "Invalid refresh token"),
    REFRESH_TOKEN_REQUIRED(1014, "Refresh token is required"),
    TOKEN_ALREADY_INVALID(1015, "Token has been disabled!"),
    BADGE_NOT_FOUND(1016, "Badge not found"),
    HASHTAG_REQUIRED(1017, "Hashtag is required"),
    CONTENT_REQUIRED(1018, "Content is required"),
    MEDIA_URL_REQUIRED(1019, "Media URL is required"),
    START_DATE_REQUIRED(1020, "Start date is required "),
    START_DATE_FUTURE_OR_PRESENT(1021, "Start date must be present or future"),
    END_DATE_REQUIRED(1022, "End date is required"),
    END_DATE_FUTURE(1023, "End date must be future"),
    END_DATE_MUST_BE_AFTER_START_DATE(1024, "End date must be after start date"),
    BADGE_IS_ACTIVE_REQUIRED(1025, "Is active is required"),
    USER_ID_REQUIRED(1026, "User ID is required"),
    CHALLENGE_NOT_FOUND(1027, "Challenge does not find"),
    BADGE_TITLE_REQUIRED(1028, "Title is required"),
    BADGE_IMAGE_REQUIRED(1029, "Image is required"),
    BADGE_DECS_REQUIRED(1030, "Description is required"),
    BADGE_CRITERIA_REQUIRED(1031, "Badge Criteria is required"),
    BADGE_POINT_REQUIRED(1032, "Point is required"),
    BADGE_START_DAY_REQUIRED(1033, "Start day is required"),
    BADGE_TITLE_EXISTS(1034, "Badge title already exists"),
    BADGE_POINT_INVALID(1036, "Points must be at least 0"),
    BADGE_CRITERIA_INVALID(1037, "Badge criteria must be at least 0"),
    POST_ID_INVALID(1038, "Post ID must be a UUID"),
    POST_NOT_FOUND(1039, "Post does not find"),
    CHALLENGE_ID_INVALID(1038, "Challenge ID must be a UUID"),
    HASHTAG_ALREADY_EXISTS_IN_DATE_RANGE(1039, "Hashtag already exists in date range"),
    UNAUTHORIZED(1040, "Please login"),
    POST_ID_REQUIRED(1041, "User ID is required"),
    CHALLENGE_OR_POST_NOT_FOUND(1042, "Challenge or post not found"),
    FEED_ID_REQUIRED(1043, "Feed ID is required"),
    FEED_ID_INVALID(1043, "Feed ID must be a UUID"),
    LIKE_ALREADY_EXISTS(1044, "Like already exists"),
    LIKE_NOT_FOUND(1045, "Like does not exist"),
    ADD_FRIEND_REQUEST_EXIST(1046, "Add friend request already exists"),
    FOLLOWER_ID_REQUIRED(1047, "Follower ID is required"),
    REQUEST_NOT_FOUND(1048, "Add friend request is not found"),
    ALREADY_FRIENDS(1049, "You are already friends"),
    CANNOT_ADD_SELF(1050, "Can not add friend for yourself"),
    FRIEND_REQUEST_NOT_ACCEPTED(1051, "You are not friends"),
    PERMISSION_DENIED(1052, "You do not have permission to perform this action"),
    START_TIME_REQUIRED(1053, "Start time is required"),
    END_TIME_REQUIRED(1054, "End time is required"),
    REPEAT_TYPE_REQUIRED(1055, "Repeat type is required"),
    REMINDER_ALREADY_EXISTS(1056, "Reminder already exist"),
    INVALID_DATE_FORMAT(1057, "Invalid date format, use YYYY-MM-DD"),
    INVALID_MONTH(1058, "Month must be between 1 and 12"),
    INVALID_DAY(1059, "Day does not exist for the given month and year"),
    REMINDER_NOT_FOUND(1060, "Reminder is not found"),
    MAPPING_FEED_ERROR(1061, "Error mapping GetFeedResponser"),
    ;

    int code;
    String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
