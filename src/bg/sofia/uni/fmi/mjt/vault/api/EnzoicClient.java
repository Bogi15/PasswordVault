package bg.sofia.uni.fmi.mjt.vault.api;

import bg.sofia.uni.fmi.mjt.vault.exceptions.PasswordCheckerException;
import bg.sofia.uni.fmi.mjt.vault.util.Hash;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EnzoicClient {
    private static final String ENDPOINT = "https://api.enzoic.com/v1/passwords";

    private static final int PARTIAL_COUNT = 10;

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_TYPE = "Basic ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APP_JSON = "application/json";

    private static final String SEPARATOR = ":";

    private static final String JSON_BODY = """
            {
              "partialSHA256": "%s"
            }
            """;


    private final HttpClient httpClient;
    private final EnzoicConfig enzoicConfig;

    public EnzoicClient(EnzoicConfig enzoicConfig) {
        this.httpClient = HttpClient.newHttpClient();
        this.enzoicConfig = enzoicConfig;
    }

    public HttpResponse<String> getResponse(String rawPassword) throws PasswordCheckerException {
        try {
            String credentials = enzoicConfig.getApiKey() + SEPARATOR + enzoicConfig.getApiSecret();
            String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            String partialHash = partial(rawPassword);
            String body = String.format(JSON_BODY, partialHash);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header(AUTHORIZATION, AUTH_TYPE + encodedAuth)
                    .header(CONTENT_TYPE, APP_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new PasswordCheckerException("Failed to call Enzoic API", e);
        }
    }

    private String partial(String rawPassword) {
        return Hash.sha256(rawPassword).substring(0, PARTIAL_COUNT);
    }
}
