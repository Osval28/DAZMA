export type DraftIssue =
  | "NAME_REQUIRED"
  | "NAME_TOO_LONG"
  | "DESCRIPTION_REQUIRED"
  | "DESCRIPTION_TOO_LONG"
  | "PRICE_INVALID"
  | "MUNICIPALITY_REQUIRED"
  | "PHOTOS_REQUIRED"
  | "PHOTOS_UPLOADING"
  | "PHOTOS_FAILED";

export type PublicationDraft = {
  name: string;
  description: string;
  price: string;
  municipality: string;
};

export type PhotoProgress = {
  uploaded: number;
  uploading: number;
  failed: number;
};

export const MAXIMUM_NAME_LENGTH = 255;
export const MAXIMUM_DESCRIPTION_LENGTH = 1000;

const MAXIMUM_PRICE = 2147483647;
const PRICE_SEPARATORS = /[\s.,$]/g;
const DIGITS_ONLY = /^\d+$/;

export function parsePrice(price: string): number | undefined {
  const digits = price.replace(PRICE_SEPARATORS, "");
  if (!DIGITS_ONLY.test(digits)) {
    return undefined;
  }
  const value = Number(digits);
  return value >= 1 && value <= MAXIMUM_PRICE ? value : undefined;
}

export function validateDraft(draft: PublicationDraft, photos: PhotoProgress): DraftIssue | undefined {
  if (draft.name.length === 0) {
    return "NAME_REQUIRED";
  }
  if (draft.name.length > MAXIMUM_NAME_LENGTH) {
    return "NAME_TOO_LONG";
  }
  if (draft.description.length === 0) {
    return "DESCRIPTION_REQUIRED";
  }
  if (draft.description.length > MAXIMUM_DESCRIPTION_LENGTH) {
    return "DESCRIPTION_TOO_LONG";
  }
  if (parsePrice(draft.price) === undefined) {
    return "PRICE_INVALID";
  }
  if (draft.municipality.length === 0) {
    return "MUNICIPALITY_REQUIRED";
  }
  if (photos.uploading > 0) {
    return "PHOTOS_UPLOADING";
  }
  if (photos.failed > 0) {
    return "PHOTOS_FAILED";
  }
  return photos.uploaded === 0 ? "PHOTOS_REQUIRED" : undefined;
}
