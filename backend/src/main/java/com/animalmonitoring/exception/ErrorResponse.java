package com.animalmonitoring.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;
    private LocalDateTime timestamp;

    public ErrorResponse() {}

    private ErrorResponse(Builder b) {
        this.status = b.status; this.error = b.error; this.message = b.message;
        this.fieldErrors = b.fieldErrors; this.timestamp = b.timestamp;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int status; private String error, message;
        private Map<String, String> fieldErrors; private LocalDateTime timestamp;
        public Builder status(int v)                        { status = v; return this; }
        public Builder error(String v)                      { error = v; return this; }
        public Builder message(String v)                    { message = v; return this; }
        public Builder fieldErrors(Map<String,String> v)    { fieldErrors = v; return this; }
        public Builder timestamp(LocalDateTime v)           { timestamp = v; return this; }
        public ErrorResponse build()                        { return new ErrorResponse(this); }
    }

    public int getStatus()                           { return status; }
    public String getError()                         { return error; }
    public String getMessage()                       { return message; }
    public Map<String, String> getFieldErrors()      { return fieldErrors; }
    public LocalDateTime getTimestamp()              { return timestamp; }
}
