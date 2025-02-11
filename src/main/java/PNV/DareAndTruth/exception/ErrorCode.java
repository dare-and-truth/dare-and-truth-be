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
    TOKEN_ALREADY_INVALID(1015, "Token has been disabled!");

    int errorCode;
    String message;

    ErrorCode(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
