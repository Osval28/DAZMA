import type { ErrorMessageKey, PublicationErrorKey } from "@/i18n/message-keys";
import { ApiError, PhotoUploadError } from "@/lib/api/errors";
import { errorKey } from "@/lib/auth/errors";
import type { DraftIssue } from "./draft-validation";
import type { PhotoIssue } from "./photo-upload";

const apiErrors: Record<string, PublicationErrorKey> = {
  PUBLICATION_OWNER_EMAIL_NOT_VERIFIED: "Publications.create.errors.emailNotVerified",
  PUBLICATION_OWNER_NOT_READY: "Publications.create.errors.ownerNotReady",
  PUBLICATION_PHOTO_UPLOAD_NOT_AVAILABLE: "Publications.create.errors.photoUploadNotAvailable",
  PUBLICATION_PHOTO_CONTENT_TYPE_NOT_ALLOWED: "Publications.create.errors.photoTypeInvalid",
  PUBLICATION_PHOTO_CONTENT_LENGTH_EXCEEDED: "Publications.create.errors.photoTooLarge",
  PUBLICATION_NAME_REQUIRED: "Publications.create.errors.nameRequired",
  PUBLICATION_NAME_TOO_LONG: "Publications.create.errors.nameTooLong",
  PUBLICATION_DESCRIPTION_REQUIRED: "Publications.create.errors.descriptionRequired",
  PUBLICATION_DESCRIPTION_TOO_LONG: "Publications.create.errors.descriptionTooLong",
  PUBLICATION_VALUE_NOT_POSITIVE: "Publications.create.errors.priceInvalid",
  PUBLICATION_PHOTOS_REQUIRED: "Publications.create.errors.photosRequired",
};

const draftErrors: Record<DraftIssue, PublicationErrorKey> = {
  NAME_REQUIRED: "Publications.create.errors.nameRequired",
  NAME_TOO_LONG: "Publications.create.errors.nameTooLong",
  DESCRIPTION_REQUIRED: "Publications.create.errors.descriptionRequired",
  DESCRIPTION_TOO_LONG: "Publications.create.errors.descriptionTooLong",
  PRICE_INVALID: "Publications.create.errors.priceInvalid",
  MUNICIPALITY_REQUIRED: "Publications.create.errors.municipalityRequired",
  PHOTOS_REQUIRED: "Publications.create.errors.photosRequired",
  PHOTOS_UPLOADING: "Publications.create.errors.photosUploading",
  PHOTOS_FAILED: "Publications.create.errors.photosFailed",
};

const photoErrors: Record<PhotoIssue, PublicationErrorKey> = {
  PHOTO_TYPE_INVALID: "Publications.create.errors.photoTypeInvalid",
  PHOTO_TOO_LARGE: "Publications.create.errors.photoTooLarge",
  PHOTOS_LIMIT_REACHED: "Publications.create.errors.photosLimitReached",
};

export function draftIssueKey(issue: DraftIssue): PublicationErrorKey {
  return draftErrors[issue];
}

export function photoIssueKey(issue: PhotoIssue): PublicationErrorKey {
  return photoErrors[issue];
}

export function publicationErrorKey(error: unknown): ErrorMessageKey {
  if (error instanceof PhotoUploadError) {
    return "Publications.create.errors.photoUploadFailed";
  }
  if (error instanceof ApiError && error.code in apiErrors) {
    return apiErrors[error.code];
  }
  return errorKey(error);
}
