package co.dazma.api.publications.services.input.dto;

import java.util.UUID;

public record RegisterPublicationOwnerInputDto(UUID userId, String userName, String externalId, String state) {
}
