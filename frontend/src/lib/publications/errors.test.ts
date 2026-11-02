import { describe, expect, it } from "vitest";
import { ApiError, NetworkError, PhotoUploadError } from "@/lib/api/errors";
import type { DraftIssue } from "./draft-validation";
import { draftIssueKey, publicationErrorKey } from "./errors";

describe("publicationErrorKey", () => {
  it.each([
    ["PUBLICATION_OWNER_EMAIL_NOT_VERIFIED", "Publications.create.errors.emailNotVerified"],
    ["PUBLICATION_OWNER_NOT_READY", "Publications.create.errors.ownerNotReady"],
    ["PUBLICATION_PHOTO_UPLOAD_NOT_AVAILABLE", "Publications.create.errors.photoUploadNotAvailable"],
    ["PUBLICATION_PHOTO_CONTENT_TYPE_NOT_ALLOWED", "Publications.create.errors.photoTypeInvalid"],
    ["PUBLICATION_PHOTO_CONTENT_LENGTH_EXCEEDED", "Publications.create.errors.photoTooLarge"],
    ["PUBLICATION_NAME_REQUIRED", "Publications.create.errors.nameRequired"],
    ["PUBLICATION_NAME_TOO_LONG", "Publications.create.errors.nameTooLong"],
    ["PUBLICATION_DESCRIPTION_REQUIRED", "Publications.create.errors.descriptionRequired"],
    ["PUBLICATION_DESCRIPTION_TOO_LONG", "Publications.create.errors.descriptionTooLong"],
    ["PUBLICATION_VALUE_NOT_POSITIVE", "Publications.create.errors.priceInvalid"],
    ["PUBLICATION_PHOTOS_REQUIRED", "Publications.create.errors.photosRequired"],
    ["PUBLICATION_PHOTOS_LIMIT_EXCEEDED", "Publications.create.errors.photosLimitReached"],
  ])("maps the API code %s", (code, expected) => {
    expect(publicationErrorKey(new ApiError(400, code))).toBe(expected);
  });

  it("maps a rejected photo upload", () => {
    expect(publicationErrorKey(new PhotoUploadError())).toBe("Publications.create.errors.photoUploadFailed");
  });

  it("falls back to the authentication errors", () => {
    expect(publicationErrorKey(new ApiError(401, "AUTHENTICATION_REQUIRED"))).toBe("Auth.errors.sessionExpired");
    expect(publicationErrorKey(new NetworkError())).toBe("Auth.errors.network");
    expect(publicationErrorKey(new Error("boom"))).toBe("Auth.errors.generic");
  });
});
describe("draftIssueKey", () => {
  const expectations: Record<DraftIssue, string> = {
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

  it.each(Object.entries(expectations))("maps %s", (issue, expected) => {
    expect(draftIssueKey(issue as DraftIssue)).toBe(expected);
  });
});
