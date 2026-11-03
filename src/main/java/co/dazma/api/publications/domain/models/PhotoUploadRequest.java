package co.dazma.api.publications.domain.models;

public record PhotoUploadRequest(String ownerExternalId, String contentType, long contentLength) {
}
