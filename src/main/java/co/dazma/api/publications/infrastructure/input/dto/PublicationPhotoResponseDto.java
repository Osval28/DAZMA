package co.dazma.api.publications.infrastructure.input.dto;

import java.net.URI;
import java.util.UUID;

public record PublicationPhotoResponseDto(UUID id, URI url, int position) {
}
