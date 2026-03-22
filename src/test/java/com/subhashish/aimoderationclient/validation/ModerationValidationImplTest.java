package com.subhashish.aimoderationclient.validation;

import com.subhashish.aimoderationclient.model.ModerationResult;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationValidationImplTest {

    @InjectMocks
    ModerationValidatorImpl moderationValidator;

    @Mock
    OkHttpClient mockHttpClient;

    @Mock
    Call mockCall;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(moderationValidator, "apiURL", "https://dummy-model-url.com");
        ReflectionTestUtils.setField(moderationValidator, "apiKey", "dummy-api-key");

        // Inject the mocked OkHttpClient to prevent actual HTTP calls
        ReflectionTestUtils.setField(moderationValidator, "httpClient", mockHttpClient);
    }

    @Test
    void testValidateWithViolationContent() throws IOException {
        String jsonResponse = "{ \"candidates\": [ { \"content\": { \"parts\": [ { \"text\": \"{\\\"flagged\\\": true, \\\"violations\\\": [\\\"spam\\\"]}\" } ] } } ] }";

        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://dummy-model-url.com?key=dummy-api-key").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(jsonResponse, MediaType.get("application/json")))
                .build();

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        ModerationResult result = moderationValidator.validate("Buy cheap rolex here!");

        assertTrue(result.flagged());
        assertEquals(1, result.violations().size());
        assertTrue(result.violations().contains("spam"));
    }

    @Test
    void testValidateWithSafeContent() throws IOException {
        String jsonResponse = "{ \"candidates\": [ { \"content\": { \"parts\": [ { \"text\": \"{\\\"flagged\\\": false, \\\"violations\\\": []}\" } ] } } ] }";

        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://dummy-model-url.com?key=dummy-api-key").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(jsonResponse, MediaType.get("application/json")))
                .build();

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        ModerationResult result = moderationValidator.validate("I want to know more details about this product");

        assertFalse(result.flagged());
        assertTrue(result.violations().isEmpty());
    }


    @Test
    void testValidateWithExceptionFromGemini() throws IOException {
        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                moderationValidator.validate("I want to know more details about this product"));

        assertEquals("Gemini moderation call failed", exception.getMessage());
    }
}
