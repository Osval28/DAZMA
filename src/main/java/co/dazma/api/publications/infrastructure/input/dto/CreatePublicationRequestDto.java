package co.dazma.api.publications.infrastructure.input.dto;

import java.util.List;
import java.util.UUID;

public record CreatePublicationRequestDto(String name, PublicationTypeDto type, String description, String municipality, List<UUID> photoUploadIds) {

}
