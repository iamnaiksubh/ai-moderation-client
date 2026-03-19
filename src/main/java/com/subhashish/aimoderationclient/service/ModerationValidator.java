package com.subhashish.aimoderationclient.service;

import com.subhashish.aimoderationclient.model.ModerationResult;

public interface ModerationValidator {
    ModerationResult validate(String text);
}