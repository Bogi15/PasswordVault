package bg.sofia.uni.fmi.mjt.vault.api;

import bg.sofia.uni.fmi.mjt.vault.exceptions.PasswordCheckerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class EnzoicPasswordCheckerTest {
    private EnzoicClient mockClient;
    private EnzoicPasswordChecker checker;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    void setUp() {
        mockClient = Mockito.mock(EnzoicClient.class);
        checker = new EnzoicPasswordChecker(mockClient);
        mockResponse = Mockito.mock(HttpResponse.class);
    }

    @Test
    void testIsPasswordCompromised_WhenPasswordNotFound_ReturnsFalse() throws PasswordCheckerException {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockClient.getResponse(anyString())).thenReturn(mockResponse);

        boolean result = checker.isPasswordCompromised("password");

        assertFalse(result, "Password not found in database should return false");
    }

    @Test
    void testIsPasswordCompromised_WhenPasswordFoundAndMatches_ReturnsTrue() throws PasswordCheckerException {
        String testPassword = "password";

        String jsonResponse = """
            {
              "candidates": [
                {
                  "sha256": "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
                  "revealedInExposure": true,
                  "exposureCount": 131
                }
              ]
            }
            """;

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockClient.getResponse(testPassword)).thenReturn(mockResponse);

        boolean result = checker.isPasswordCompromised(testPassword);

        assertTrue(result, "When the hash match we return true");
    }

    @Test
    void testIsPasswordCompromised_WhenPasswordFoundButNoMatch_ReturnsFalse() throws PasswordCheckerException {
        String testPassword = "password";

        String jsonResponse = """
            {
              "candidates": [
                {
                  "sha256": "somethingwrong",
                  "revealedInExposure": true,
                  "exposureCount": 17
                }
              ]
            }
            """;

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockClient.getResponse(testPassword)).thenReturn(mockResponse);

        boolean result = checker.isPasswordCompromised(testPassword);

        assertFalse(result, "When the hash doesnt match we return false");
    }

    @Test
    void testIsPasswordCompromised_WhenCandidatesNull_ReturnsFalse() throws PasswordCheckerException {
        String jsonResponse = """
            {
              "candidates": null
            }
            """;

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockClient.getResponse(anyString())).thenReturn(mockResponse);

        boolean result = checker.isPasswordCompromised("password");

        assertFalse(result, "When we have null candidates return false");
    }
}