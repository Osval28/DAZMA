import { NetworkError, PhotoUploadError } from "@/lib/api/errors";
import { authorizePhotoUpload } from "@/lib/api/publications";

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
