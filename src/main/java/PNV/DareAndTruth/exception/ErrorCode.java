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
    BADGE_POINT_INVALID(1035, "Points must be at least 0"),
    BADGE_CRITERIA_INVALID(1036, "Badge criteria must be at least 0"),
    POST_ID_INVALID(1037, "Post ID must be a UUID"),
    POST_NOT_FOUND(1038, "Post does not find"),
    CHALLENGE_ID_INVALID(1039, "Challenge ID must be a UUID"),
    HASHTAG_ALREADY_EXISTS_IN_DATE_RANGE(1040, "Hashtag already exists in date range"),
    UNAUTHORIZED(1041, "Please login"),
    POST_ID_REQUIRED(1042, "User ID is required"),
    CHALLENGE_OR_POST_NOT_FOUND(1043,"Challenge or post not found"),
    FEED_ID_REQUIRED(1044,"Feed ID is required"),
    FEED_ID_INVALID(1045,"Feed ID must be a UUID"),
    LIKE_ALREADY_EXISTS(1046,"Like already exists"),
    LIKE_NOT_FOUND(1047,"Like does not exist"),
    ADD_FRIEND_REQUEST_EXIST(1049, "Add friend request already exists"),
    FOLLOWER_ID_REQUIRED(1050, "Follower ID is required"),
    REQUEST_NOT_FOUND(1051, "Add friend request is not found"),
    ALREADY_FRIENDS(1052,"You are already friends"),
    CANNOT_ADD_SELF(1053,"Can not add friend for yourself" ),
    FRIEND_REQUEST_NOT_ACCEPTED(1054,"You are not friends" ),
    PERMISSION_DENIED(1055,"You do not have permission to perform this action" ),
    ;

    int code;
    String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
