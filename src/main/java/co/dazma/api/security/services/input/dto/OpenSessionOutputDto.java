package co.dazma.api.security.services.input.dto;

import java.time.Duration;

public record OpenSessionOutputDto(String sessionToken, Duration validity) {
}
