import { afterEach, expect, test, vi } from "vitest";
import { PhotoUploadError } from "@/lib/api/errors";
import { uploadPhoto } from "./photo-upload";
import { authorizePhotoUpload, getPhotoInspectionStatus } from "@/lib/api/publications";

vi.mock("@/lib/api/publications", () => ({
  authorizePhotoUpload: vi.fn(),
  getPhotoInspectionStatus: vi.fn(),
}));

afterEach(() => {
  vi.useRealTimers();
  vi.unstubAllGlobals();
});

test("photo ID is returned only after the scan reports READY", async () => {
  vi.useFakeTimers();
  vi.mocked(authorizePhotoUpload).mockResolvedValue({ photoId: "photo-1", uploadUrl: "https://r2.example/upload", expiresAt: "later" });
  vi.mocked(getPhotoInspectionStatus)
    .mockResolvedValueOnce({ status: "PENDING" })
    .mockResolvedValueOnce({ status: "READY" });
  vi.stubGlobal("fetch", vi.fn().mockResolvedValue(new Response(null, { status: 200 })));

  const result = uploadPhoto(new File(["photo"], "photo.jpg", { type: "image/jpeg" }));
  await vi.advanceTimersByTimeAsync(0);
  expect(getPhotoInspectionStatus).toHaveBeenCalledTimes(1);
  await vi.advanceTimersByTimeAsync(2_000);
  await expect(result).resolves.toBe("photo-1");
});

test("a rejected photo is never accepted", async () => {
  vi.mocked(authorizePhotoUpload).mockResolvedValue({ photoId: "photo-2", uploadUrl: "https://r2.example/upload", expiresAt: "later" });
  vi.mocked(getPhotoInspectionStatus).mockResolvedValue({ status: "REJECTED" });
  vi.stubGlobal("fetch", vi.fn().mockResolvedValue(new Response(null, { status: 200 })));

  await expect(uploadPhoto(new File(["photo"], "photo.jpg", { type: "image/jpeg" })))
    .rejects.toBeInstanceOf(PhotoUploadError);
});
