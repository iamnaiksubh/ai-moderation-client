package com.subhashish.aimoderationclient.model;

import java.util.List;

public record ModerationResult(boolean flagged, List<String> violations) {
    @Override
    public String toString() {
        return flagged ? "Flagged: " + violations : "Safe";
    }
}