import { NetworkError, PhotoUploadError } from "@/lib/api/errors";
import { authorizePhotoUpload, getPhotoInspectionStatus } from "@/lib/api/publications";

const SCAN_TIMEOUT_MS = 90_000;
const SCAN_POLL_INTERVAL_MS = 2_000;

async function waitForInspection(photoId: string): Promise<void> {
  const deadline = Date.now() + SCAN_TIMEOUT_MS;
  while (Date.now() < deadline) {
    const { status } = await getPhotoInspectionStatus(photoId);
    if (status === "READY") return;
    if (status === "REJECTED") throw new PhotoUploadError();
    await new Promise((resolve) => setTimeout(resolve, SCAN_POLL_INTERVAL_MS));
  }
  throw new PhotoUploadError();
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
  await waitForInspection(authorization.photoId);
  return authorization.photoId;
}
