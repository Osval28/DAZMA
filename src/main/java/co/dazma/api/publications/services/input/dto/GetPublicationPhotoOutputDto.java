package co.dazma.api.publications.services.input.dto;

import java.net.URI;
import java.util.UUID;

public record GetPublicationPhotoOutputDto(UUID id, URI url, int position) {
}
