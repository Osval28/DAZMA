import { NetworkError, PhotoUploadError } from "@/lib/api/errors";
import { authorizePhotoUpload } from "@/lib/api/publications";

export type PhotoIssue = "PHOTO_TYPE_INVALID" | "PHOTO_TOO_LARGE" | "PHOTOS_LIMIT_REACHED";

export const MAXIMUM_PHOTOS = 5;
export const MAXIMUM_PHOTO_MEGABYTES = 5;
export const ALLOWED_PHOTO_TYPES = ["image/jpeg", "image/png", "image/webp"];

const MAXIMUM_PHOTO_BYTES = MAXIMUM_PHOTO_MEGABYTES * 1024 * 1024;

export function validatePhotoFile(file: File): PhotoIssue | undefined {
  if (!ALLOWED_PHOTO_TYPES.includes(file.type)) {
    return "PHOTO_TYPE_INVALID";
  }
  return file.size > MAXIMUM_PHOTO_BYTES ? "PHOTO_TOO_LARGE" : undefined;
}

export async function uploadPhoto(file: File): Promise<string> {
  const authorization = await authorizePhotoUpload(file.type, file.size);
  let response: Response;
  try {
    response = await fetch(authorization.uploadUrl, {
      method: "PUT",
      headers: { "Content-Type": file.type },
      body: file,
    });
  } catch {
    throw new NetworkError();
  }
  if (!response.ok) {
    throw new PhotoUploadError();
  }
  return authorization.photoId;
}
