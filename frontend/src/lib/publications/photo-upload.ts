import { NetworkError, PhotoUploadError } from "@/lib/api/errors";
import { authorizePhotoUpload } from "@/lib/api/publications";

export type PhotoIssue = "PHOTO_TYPE_INVALID" | "PHOTOS_LIMIT_REACHED";

export const MAXIMUM_PHOTOS = 5;
export const ALLOWED_PHOTO_TYPES = ["image/jpeg", "image/png", "image/webp"];

export function validatePhotoFile(file: File): PhotoIssue | undefined {
  return ALLOWED_PHOTO_TYPES.includes(file.type) ? undefined : "PHOTO_TYPE_INVALID";
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
