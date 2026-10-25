package co.dazma.api.publications.services.input.dto;

public record AuthorizePublicationPhotoUploadInputDto(
        String ownerExternalId,
        String contentType,
        long contentLength
) {
}
