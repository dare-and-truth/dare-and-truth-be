package PNV.DareAndTruth.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ApiStatus {
    SUCCESS,
    FAIL;

    @JsonValue
    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
