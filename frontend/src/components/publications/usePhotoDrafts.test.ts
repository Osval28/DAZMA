import { act, renderHook, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, NetworkError, PhotoUploadError } from "@/lib/api/errors";

const { uploadPhoto } = vi.hoisted(() => ({ uploadPhoto: vi.fn() }));

vi.mock("@/lib/publications/photo-upload", async (importOriginal) => ({
  ...(await importOriginal<typeof import("@/lib/publications/photo-upload")>()),
  uploadPhoto,
}));

import { usePhotoDrafts } from "./usePhotoDrafts";

let previewCounter = 0;
const revokeObjectURL = vi.fn();

function image(name = "foto.png", type = "image/png"): File {
  return new File(["x"], name, { type });
}

describe("usePhotoDrafts", () => {
  beforeEach(() => {
    uploadPhoto.mockReset();
    revokeObjectURL.mockReset();
    previewCounter = 0;
    URL.createObjectURL = vi.fn(() => `blob:preview-${(previewCounter += 1)}`);
    URL.revokeObjectURL = revokeObjectURL;
  });

  it("starts empty", () => {
    const { result } = renderHook(() => usePhotoDrafts());

    expect(result.current.photos).toEqual([]);
    expect(result.current.uploadedIds).toEqual([]);
    expect(result.current.issue).toBeUndefined();
  });

  it("shows a photo as uploading and then as uploaded", async () => {
    uploadPhoto.mockResolvedValue("photo-1");
    const { result } = renderHook(() => usePhotoDrafts());

    act(() => result.current.add([image()]));

    expect(result.current.photos[0].upload).toEqual({ status: "uploading" });
    expect(result.current.photos[0].previewUrl).toBe("blob:preview-1");
    expect(result.current.uploadingCount).toBe(1);
    await waitFor(() => expect(result.current.uploadedIds).toEqual(["photo-1"]));
    expect(result.current.uploadingCount).toBe(0);
  });

  it("lets the backend decide which photo types are supported", async () => {
    uploadPhoto.mockRejectedValue(new ApiError(400, "PUBLICATION_PHOTO_CONTENT_TYPE_NOT_ALLOWED"));
    const { result } = renderHook(() => usePhotoDrafts());

    act(() => result.current.add([image("nota.pdf", "application/pdf")]));

    await waitFor(() => expect(result.current.issue).toBe("Publications.create.errors.photoTypeInvalid"));
    expect(result.current.photos).toHaveLength(1);
    expect(uploadPhoto).toHaveBeenCalled();
  });

  it("submits each selected file for backend validation", async () => {
    uploadPhoto.mockResolvedValue("photo-1");
    const { result } = renderHook(() => usePhotoDrafts());

    act(() => result.current.add([image("nota.pdf", "application/pdf"), image()]));

    expect(result.current.photos).toHaveLength(2);
    await waitFor(() => expect(result.current.uploadedIds).toHaveLength(2));
  });

  it("leaves the photo count decision to the backend", async () => {
    uploadPhoto.mockResolvedValue("photo");
    const { result } = renderHook(() => usePhotoDrafts());

    act(() => result.current.add(Array.from({ length: 6 }, (_, index) => image(`${index}.png`))));

    expect(result.current.photos).toHaveLength(6);
    await waitFor(() => expect(result.current.uploadingCount).toBe(0));
  });

  it("clears the previous issue when new files are added", async () => {
    uploadPhoto.mockRejectedValueOnce(new ApiError(400, "PUBLICATION_PHOTO_CONTENT_TYPE_NOT_ALLOWED")).mockResolvedValueOnce("photo-1");
    const { result } = renderHook(() => usePhotoDrafts());
    act(() => result.current.add([image("nota.pdf", "application/pdf")]));
    await waitFor(() => expect(result.current.issue).toBe("Publications.create.errors.photoTypeInvalid"));

    act(() => result.current.add([image()]));

    expect(result.current.issue).toBeUndefined();
    await waitFor(() => expect(result.current.uploadedIds).toHaveLength(1));
  });

  it("marks a photo as failed with the reason", async () => {
    uploadPhoto.mockRejectedValue(new PhotoUploadError());
    const { result } = renderHook(() => usePhotoDrafts());

    act(() => result.current.add([image()]));

    await waitFor(() => expect(result.current.failedCount).toBe(1));
    expect(result.current.photos[0].upload).toEqual({ status: "failed", reason: "Publications.create.errors.photoUploadFailed" });
  });

  it("announces why the backend rejected a photo", async () => {
    uploadPhoto.mockRejectedValue(new ApiError(422, "PUBLICATION_PHOTO_CONTENT_LENGTH_EXCEEDED"));
    const { result } = renderHook(() => usePhotoDrafts());

    act(() => result.current.add([image()]));

    await waitFor(() => expect(result.current.failedCount).toBe(1));
    expect(result.current.issue).toBe("Publications.create.errors.photoTooLarge");
  });

  it("clears the announced problem when a failed photo is retried", async () => {
    uploadPhoto.mockRejectedValueOnce(new NetworkError()).mockResolvedValueOnce("photo-2");
    const { result } = renderHook(() => usePhotoDrafts());
    act(() => result.current.add([image()]));
    await waitFor(() => expect(result.current.issue).toBeDefined());

    act(() => result.current.retry(result.current.photos[0].key));

    expect(result.current.issue).toBeUndefined();
    await waitFor(() => expect(result.current.uploadedIds).toEqual(["photo-2"]));
  });

  it("retries a failed photo", async () => {
    uploadPhoto.mockRejectedValueOnce(new NetworkError()).mockResolvedValueOnce("photo-2");
    const { result } = renderHook(() => usePhotoDrafts());
    act(() => result.current.add([image()]));
    await waitFor(() => expect(result.current.failedCount).toBe(1));

    act(() => result.current.retry(result.current.photos[0].key));

    await waitFor(() => expect(result.current.uploadedIds).toEqual(["photo-2"]));
    expect(result.current.failedCount).toBe(0);
    expect(uploadPhoto).toHaveBeenCalledTimes(2);
  });

  it("ignores a retry for an unknown photo", () => {
    const { result } = renderHook(() => usePhotoDrafts());

    act(() => result.current.retry("unknown"));

    expect(uploadPhoto).not.toHaveBeenCalled();
  });

  it("removes a photo and releases its preview", async () => {
    uploadPhoto.mockResolvedValue("photo-1");
    const { result } = renderHook(() => usePhotoDrafts());
    act(() => result.current.add([image()]));
    await waitFor(() => expect(result.current.uploadedIds).toHaveLength(1));

    act(() => result.current.remove(result.current.photos[0].key));

    expect(result.current.photos).toEqual([]);
    expect(revokeObjectURL).toHaveBeenCalledWith("blob:preview-1");
  });

  it("moves a photo to the front when it becomes the cover", async () => {
    uploadPhoto.mockResolvedValueOnce("photo-1").mockResolvedValueOnce("photo-2");
    const { result } = renderHook(() => usePhotoDrafts());
    act(() => result.current.add([image("a.png"), image("b.png")]));
    await waitFor(() => expect(result.current.uploadedIds).toHaveLength(2));

    act(() => result.current.makeCover(result.current.photos[1].key));

    expect(result.current.photos.map((photo) => photo.file.name)).toEqual(["b.png", "a.png"]);
    expect(result.current.uploadedIds).toEqual(["photo-2", "photo-1"]);
  });

  it("ignores a cover request for an unknown photo", async () => {
    uploadPhoto.mockResolvedValue("photo-1");
    const { result } = renderHook(() => usePhotoDrafts());
    act(() => result.current.add([image()]));
    await waitFor(() => expect(result.current.uploadedIds).toHaveLength(1));

    act(() => result.current.makeCover("unknown"));

    expect(result.current.photos).toHaveLength(1);
  });

  it("releases every preview when the form goes away", async () => {
    uploadPhoto.mockResolvedValue("photo");
    const { result, unmount } = renderHook(() => usePhotoDrafts());
    act(() => result.current.add([image("a.png"), image("b.png")]));
    await waitFor(() => expect(result.current.uploadedIds).toHaveLength(2));

    unmount();

    expect(revokeObjectURL).toHaveBeenCalledWith("blob:preview-1");
    expect(revokeObjectURL).toHaveBeenCalledWith("blob:preview-2");
  });
});
