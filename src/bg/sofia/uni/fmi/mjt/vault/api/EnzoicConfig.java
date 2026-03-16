package bg.sofia.uni.fmi.mjt.vault.api;

public class EnzoicConfig {

    private static final String API_KEY_ENV = "ENZOIC_API_KEY";
    private static final String API_SECRET_ENV = "ENZOIC_API_SECRET";

    private final String apiKey;
    private final String apiSecret;

    private static final String NO_ENVIRONMENT_VARIABLES =
            "Enzoic API credentials are not configured in environment variables.";

    public  EnzoicConfig() {
        this.apiKey = System.getenv(API_KEY_ENV);
        this.apiSecret = System.getenv(API_SECRET_ENV);

        if (apiKey == null || apiSecret == null) {
            throw new IllegalStateException(NO_ENVIRONMENT_VARIABLES);
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}

