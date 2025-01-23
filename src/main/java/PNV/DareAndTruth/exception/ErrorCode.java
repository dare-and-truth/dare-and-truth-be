package PNV.DareAndTruth.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1001, "An unexpected error has occurred."),
    EMAIL_INVALID(1002, "Wrong email format."),
    EMAIL_EXISTS(1003, "Email already exists."),
    USERNAME_INVALID(1004, "Username must be at least 3 characters long."),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters long."),
    USER_NOT_FOUND(1006, "User does not find."),
    USER_ID_INVALID(1007, "User ID must be a UUID."),
    EMAIL_REQUIRED(1008, "Email is required."),
    USERNAME_REQUIRED(1009, "Username is required."),
    PASSWORD_REQUIRED(1010, "Password is required."),
    ;

    int errorCode;
    String message;

    ErrorCode(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
