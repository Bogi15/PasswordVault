package bg.sofia.uni.fmi.mjt.vault.api.response;

public record LeakedPasswordCandidate(String sha256, boolean revealedInExposure, int exposureCount) {
}
