package co.dazma.api.publications.domain;

public record PublicationRules(int maximumPhotos, int maximumPageSize, int defaultPageSize) {

    public PublicationRules {
        if (maximumPhotos < 1 || maximumPageSize < 1 || defaultPageSize < 1 || defaultPageSize > maximumPageSize) {
            throw new IllegalArgumentException("Invalid publication rules configuration");
        }
    }
}
