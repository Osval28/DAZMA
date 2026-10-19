package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum PublicationErrorCode implements ErrorCode {

    ID_REQUIRED(
            "PUBLICATION_ID_REQUIRED",
            "publication.id.required"
    ),
    OWNER_REQUIRED(
            "PUBLICATION_OWNER_REQUIRED",
            "publication.owner.required"
    ),
    NAME_REQUIRED(
            "PUBLICATION_NAME_REQUIRED",
            "publication.name.required"
    ),
    TYPE_REQUIRED(
            "PUBLICATION_TYPE_REQUIRED",
            "publication.type.required"
    ),
    DESCRIPTION_REQUIRED(
            "PUBLICATION_DESCRIPTION_REQUIRED",
            "publication.description.required"
    ),
    CITY_REQUIRED(
            "PUBLICATION_CITY_REQUIRED",
            "publication.city.required"
    ),
    STATUS_REQUIRED(
            "PUBLICATION_STATUS_REQUIRED",
            "publication.status.required"
    ),
    CREATED_AT_REQUIRED(
            "PUBLICATION_CREATED_AT_REQUIRED",
            "publication.created-at.required"
    ),
    VALUE_REQUIRED(
            "PUBLICATION_VALUE_REQUIRED",
            "publication.value.required"
    ),
    VALUE_NOT_POSITIVE(
            "PUBLICATION_VALUE_NOT_POSITIVE",
            "publication.value.not-positive"
    ),
    PHOTOS_REQUIRED(
            "PUBLICATION_PHOTOS_REQUIRED",
            "publication.photos.required"
    ),
    PHOTO_NULL(
            "PUBLICATION_PHOTO_NULL",
            "publication.photo.null"
    ),
    PHOTO_DUPLICATED(
            "PUBLICATION_PHOTO_DUPLICATED",
            "publication.photo.duplicated"
    ),
    PHOTO_POSITION_DUPLICATED(
            "PUBLICATION_PHOTO_POSITION_DUPLICATED",
            "publication.photo.position-duplicated"
    ),
    PHOTO_ID_REQUIRED(
            "PUBLICATION_PHOTO_ID_REQUIRED",
            "publication.photo.id.required"
    ),
    PHOTO_STORAGE_REFERENCE_REQUIRED(
            "PUBLICATION_PHOTO_STORAGE_REFERENCE_REQUIRED",
            "publication.photo.storage-reference.required"
    ),
    PHOTO_POSITION_INVALID(
            "PUBLICATION_PHOTO_POSITION_INVALID",
            "publication.photo.position.invalid"
    ),
    NOT_FOUND(
            "PUBLICATION_NOT_FOUND",
            "publication.not-found"
    );

    private final String code;
    private final String messageKey;

    PublicationErrorCode(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageKey() {
        return messageKey;
    }
}
