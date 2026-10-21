package co.dazma.api.publications.infrastructure.input.dto;

public record AuthorizePublicationPhotoUploadRequestDto(
        String contentType,
        long contentLength
) {
}
