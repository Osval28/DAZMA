package co.dazma.api.publications.infrastructure.output.parameters;

import co.dazma.api.shared.services.output.ports.ParameterKey;

public enum PublicationParameter implements ParameterKey {

    PHOTO_ALLOWED_CONTENT_TYPES("dazma.publications.photo-upload.allowed-content-types"),
    PHOTO_MAXIMUM_SIZE("dazma.publications.photo-upload.maximum-size"),
    PHOTO_UPLOAD_VALIDITY("dazma.publications.photo-upload.validity"),
    PHOTO_DOWNLOAD_VALIDITY("dazma.publications.photo-download.validity");

    private final String key;

    PublicationParameter(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
