package bg.sofia.uni.fmi.mjt.vault.api.response;

import java.util.List;

public record Response(List<LeakedPasswordCandidate> candidates) {
}
