import { afterEach, describe, expect, it, vi } from "vitest";
import { NetworkError, PhotoUploadError } from "@/lib/api/errors";

const { authorizePhotoUpload } = vi.hoisted(() => ({ authorizePhotoUpload: vi.fn() }));

vi.mock("@/lib/api/publications", () => ({ authorizePhotoUpload }));

import { ALLOWED_PHOTO_TYPES, MAXIMUM_PHOTOS, uploadPhoto, validatePhotoFile } from "./photo-upload";

const fetchMock = vi.fn<typeof fetch>();

function fileOf(type: string, size = 1024): File {
  const file = new File(["x"], "photo", { type });
  Object.defineProperty(file, "size", { value: size });
  return file;
}

describe("validatePhotoFile", () => {
  it.each(ALLOWED_PHOTO_TYPES)("accepts %s", (type) => {
    expect(validatePhotoFile(fileOf(type))).toBeUndefined();
  });

  it("rejects other file types", () => {
    expect(validatePhotoFile(fileOf("image/gif"))).toBe("PHOTO_TYPE_INVALID");
    expect(validatePhotoFile(fileOf("application/pdf"))).toBe("PHOTO_TYPE_INVALID");
  });

  it("leaves the size decision to the backend", () => {
    expect(validatePhotoFile(fileOf("image/png", 50 * 1024 * 1024))).toBeUndefined();
  });

  it("limits the number of photos", () => {
    expect(MAXIMUM_PHOTOS).toBe(5);
  });
});

describe("uploadPhoto", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    fetchMock.mockReset();
    authorizePhotoUpload.mockReset();
  });

  it("uploads the file to the authorized url and returns the photo id", async () => {
    vi.stubGlobal("fetch", fetchMock);
    authorizePhotoUpload.mockResolvedValue({ photoId: "photo-1", uploadUrl: "https://storage.test/put" });
    fetchMock.mockResolvedValue(new Response(null, { status: 200 }));
    const file = fileOf("image/png", 2048);

    expect(await uploadPhoto(file)).toBe("photo-1");

    expect(authorizePhotoUpload).toHaveBeenCalledWith("image/png", 2048);
    expect(fetchMock).toHaveBeenCalledWith("https://storage.test/put", {
      method: "PUT",
      headers: { "Content-Type": "image/png" },
      body: file,
    });
  });

  it("fails when the storage rejects the photo", async () => {
    vi.stubGlobal("fetch", fetchMock);
    authorizePhotoUpload.mockResolvedValue({ photoId: "photo-1", uploadUrl: "https://storage.test/put" });
    fetchMock.mockResolvedValue(new Response(null, { status: 403 }));

    await expect(uploadPhoto(fileOf("image/png"))).rejects.toBeInstanceOf(PhotoUploadError);
  });

  it("reports a network error when the storage cannot be reached", async () => {
    vi.stubGlobal("fetch", fetchMock);
    authorizePhotoUpload.mockResolvedValue({ photoId: "photo-1", uploadUrl: "https://storage.test/put" });
    fetchMock.mockRejectedValue(new TypeError("offline"));

    await expect(uploadPhoto(fileOf("image/png"))).rejects.toBeInstanceOf(NetworkError);
  });

  it("does not upload when the authorization fails", async () => {
    vi.stubGlobal("fetch", fetchMock);
    authorizePhotoUpload.mockRejectedValue(new Error("denied"));

    await expect(uploadPhoto(fileOf("image/png"))).rejects.toThrow("denied");
    expect(fetchMock).not.toHaveBeenCalled();
  });
});
