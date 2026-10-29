package co.dazma.api.publications.services.input.dto;

import java.util.Optional;

public record ListPublicationsInputDto(Optional<String> municipality, int page, int size) {
}
