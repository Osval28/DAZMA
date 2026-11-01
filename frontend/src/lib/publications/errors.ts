import { ApiError, PhotoUploadError } from "@/lib/api/errors";
import { describeError } from "@/lib/auth/errors";
import type { DraftIssue } from "./draft-validation";
import { publicationMessages } from "./messages";
import type { PhotoIssue } from "./photo-upload";

const messages = publicationMessages.create.errors;

const apiMessages: Record<string, string> = {
  PUBLICATION_OWNER_EMAIL_NOT_VERIFIED: messages.emailNotVerified,
  PUBLICATION_OWNER_NOT_READY: messages.ownerNotReady,
  PUBLICATION_PHOTO_UPLOAD_NOT_AVAILABLE: messages.photoUploadNotAvailable,
  PUBLICATION_PHOTO_CONTENT_TYPE_NOT_ALLOWED: messages.photoTypeInvalid,
  PUBLICATION_PHOTO_CONTENT_LENGTH_EXCEEDED: messages.photoTooLarge,
  PUBLICATION_NAME_REQUIRED: messages.nameRequired,
  PUBLICATION_NAME_TOO_LONG: messages.nameTooLong,
  PUBLICATION_DESCRIPTION_REQUIRED: messages.descriptionRequired,
  PUBLICATION_DESCRIPTION_TOO_LONG: messages.descriptionTooLong,
  PUBLICATION_VALUE_NOT_POSITIVE: messages.priceInvalid,
  PUBLICATION_PHOTOS_REQUIRED: messages.photosRequired,
};

const draftMessages: Record<DraftIssue, string> = {
  NAME_REQUIRED: messages.nameRequired,
  NAME_TOO_LONG: messages.nameTooLong,
  DESCRIPTION_REQUIRED: messages.descriptionRequired,
  DESCRIPTION_TOO_LONG: messages.descriptionTooLong,
  PRICE_INVALID: messages.priceInvalid,
  MUNICIPALITY_REQUIRED: messages.municipalityRequired,
  PHOTOS_REQUIRED: messages.photosRequired,
  PHOTOS_UPLOADING: messages.photosUploading,
  PHOTOS_FAILED: messages.photosFailed,
};

const photoMessages: Record<PhotoIssue, string> = {
  PHOTO_TYPE_INVALID: messages.photoTypeInvalid,
  PHOTO_TOO_LARGE: messages.photoTooLarge,
  PHOTOS_LIMIT_REACHED: messages.photosLimitReached,
};

export function describeDraftIssue(issue: DraftIssue): string {
  return draftMessages[issue];
}

export function describePhotoIssue(issue: PhotoIssue): string {
  return photoMessages[issue];
}

export function describePublicationError(error: unknown): string {
  if (error instanceof PhotoUploadError) {
    return messages.photoUploadFailed;
  }
  if (error instanceof ApiError && error.code in apiMessages) {
    return apiMessages[error.code];
  }
  return describeError(error);
}
