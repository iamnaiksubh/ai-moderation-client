package com.subhashish.aimoderationclient.controller;

import com.subhashish.aimoderationclient.model.ModerationResult;
import com.subhashish.aimoderationclient.service.ModerationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiModerationController {

    @Autowired
    ModerationValidator moderationValidator;

    @GetMapping("/checkModeration")
    public ResponseEntity<ModerationResult> checkModeration(@RequestParam(name = "prompt") String prompt) {
        return ResponseEntity.ok(moderationValidator.validate(prompt));
    }

}
