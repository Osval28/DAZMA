package co.dazma.api.publications.services.input.dto;

import java.util.UUID;

public record RegisterPublicationOwnerInputDto(UUID userId, String externalId, String state) {
}
