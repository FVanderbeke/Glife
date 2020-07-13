package io.vanderbeke.glife.api.exception;

public class UnknownRuleSetException extends Exception {
    private final String id;

    public UnknownRuleSetException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
