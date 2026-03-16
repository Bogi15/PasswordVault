package bg.sofia.uni.fmi.mjt.vault.api;

import bg.sofia.uni.fmi.mjt.vault.api.response.LeakedPasswordCandidate;
import bg.sofia.uni.fmi.mjt.vault.api.response.Response;
import bg.sofia.uni.fmi.mjt.vault.exceptions.PasswordCheckerException;
import bg.sofia.uni.fmi.mjt.vault.util.Hash;
import com.google.gson.Gson;

import java.net.http.HttpResponse;

public class EnzoicPasswordChecker implements PasswordLeakedChecker {

    private final EnzoicClient client;
    private final Gson gson;

    private static final int FOUND_STATUS_CODE = 200;
    private static final int NOT_FOUND_STATUS_CODE = 404;

    public EnzoicPasswordChecker(EnzoicClient client) {
        this.client = client;
        this.gson = new Gson();
    }

    @Override
    public boolean isPasswordCompromised(String rawPassword) throws PasswordCheckerException {
        try {
            HttpResponse<String> response = client.getResponse(rawPassword);

            if (response.statusCode() == NOT_FOUND_STATUS_CODE) {
                return false;
            }

            if (response.statusCode() != FOUND_STATUS_CODE) {
                throw new PasswordCheckerException("Unexpected HTTP status: " + response.statusCode());
            }

            Response apiResponse = gson.fromJson(response.body(), Response.class);

            if (apiResponse.candidates() == null || apiResponse.candidates().isEmpty()) {
                return false;
            }

            String fullSha = Hash.sha256(rawPassword);

            return apiResponse.candidates()
                    .stream()
                    .map(LeakedPasswordCandidate::sha256)
                    .anyMatch(fullSha::equalsIgnoreCase);
        } catch (Exception e) {
            throw new PasswordCheckerException("Failed to check password: " + e.getMessage(), e);
        }
    }
}
