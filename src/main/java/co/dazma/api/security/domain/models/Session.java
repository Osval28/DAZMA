package co.dazma.api.security.domain.models;

import java.time.Duration;

public record Session(String token, Duration validity) {
}
