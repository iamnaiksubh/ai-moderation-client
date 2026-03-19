package com.subhashish.aimoderationclient.validation;

import com.subhashish.aimoderationclient.model.ModerationResult;

public interface ModerationValidator {
    ModerationResult validate(String text);
}