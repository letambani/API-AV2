package br.fmp.av2.exception;

import java.time.Instant;
import java.util.List;

public class ApiError {
    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldErrorItem> fieldErrors;

    public ApiError(int status, String error, String message, String path, List<FieldErrorItem> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public static ApiError from(int status, String error, String message, String path) {
        return new ApiError(status, error, message, path, null);
    }

    public static ApiError from(int status, String error, String message, String path, List<FieldErrorItem> fieldErrors) {
        return new ApiError(status, error, message, path, fieldErrors);
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<FieldErrorItem> getFieldErrors() { return fieldErrors; }

    public static class FieldErrorItem {
        private String field;
        private String message;

        public FieldErrorItem(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}
