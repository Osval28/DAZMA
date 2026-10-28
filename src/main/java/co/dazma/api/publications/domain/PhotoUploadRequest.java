package co.dazma.api.publications.domain;

public record PhotoUploadRequest(String ownerExternalId, String contentType, long contentLength) {
}
