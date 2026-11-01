"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import type { ErrorMessageKey, PublicationErrorKey } from "@/i18n/message-keys";
import { photoIssueKey, publicationErrorKey } from "@/lib/publications/errors";
import { MAXIMUM_PHOTOS, uploadPhoto, validatePhotoFile } from "@/lib/publications/photo-upload";

type UploadState = { status: "uploading" } | { status: "uploaded"; photoId: string } | { status: "failed"; reason: ErrorMessageKey };

export type DraftPhoto = {
  key: string;
  file: File;
  previewUrl: string;
  upload: UploadState;
};

export type PhotoDrafts = {
  photos: DraftPhoto[];
  uploadedIds: string[];
  uploadingCount: number;
  failedCount: number;
  issue: PublicationErrorKey | undefined;
  add: (files: File[]) => void;
  remove: (key: string) => void;
  makeCover: (key: string) => void;
  retry: (key: string) => void;
};

export function usePhotoDrafts(): PhotoDrafts {
  const [photos, setPhotos] = useState<DraftPhoto[]>([]);
  const [issue, setIssue] = useState<PublicationErrorKey>();
  const latestPhotos = useRef<DraftPhoto[]>([]);

  useEffect(() => {
    latestPhotos.current = photos;
  }, [photos]);

  useEffect(
    () => () => {
      latestPhotos.current.forEach((photo) => URL.revokeObjectURL(photo.previewUrl));
    },
    [],
  );

  const updateUpload = useCallback((key: string, upload: UploadState) => {
    setPhotos((current) => current.map((photo) => (photo.key === key ? { ...photo, upload } : photo)));
  }, []);

  const startUpload = useCallback(
    (key: string, file: File) => {
      uploadPhoto(file)
        .then((photoId) => updateUpload(key, { status: "uploaded", photoId }))
        .catch((error) => updateUpload(key, { status: "failed", reason: publicationErrorKey(error) }));
    },
    [updateUpload],
  );

  const add = useCallback(
    (files: File[]) => {
      setIssue(undefined);
      const accepted: DraftPhoto[] = [];
      for (const file of files) {
        if (photos.length + accepted.length >= MAXIMUM_PHOTOS) {
          setIssue(photoIssueKey("PHOTOS_LIMIT_REACHED"));
          break;
        }
        const fileIssue = validatePhotoFile(file);
        if (fileIssue !== undefined) {
          setIssue(photoIssueKey(fileIssue));
          continue;
        }
        accepted.push({ key: crypto.randomUUID(), file, previewUrl: URL.createObjectURL(file), upload: { status: "uploading" } });
      }
      setPhotos((current) => [...current, ...accepted]);
      accepted.forEach((photo) => startUpload(photo.key, photo.file));
    },
    [photos.length, startUpload],
  );

  const remove = useCallback((key: string) => {
    setIssue(undefined);
    latestPhotos.current.filter((photo) => photo.key === key).forEach((photo) => URL.revokeObjectURL(photo.previewUrl));
    setPhotos((current) => current.filter((photo) => photo.key !== key));
  }, []);

  const makeCover = useCallback((key: string) => {
    setPhotos((current) => {
      const chosen = current.find((photo) => photo.key === key);
      return chosen === undefined ? current : [chosen, ...current.filter((photo) => photo.key !== key)];
    });
  }, []);

  const retry = useCallback(
    (key: string) => {
      const photo = latestPhotos.current.find((candidate) => candidate.key === key);
      if (photo === undefined) {
        return;
      }
      updateUpload(key, { status: "uploading" });
      startUpload(key, photo.file);
    },
    [startUpload, updateUpload],
  );

  const uploadedIds = photos.flatMap((photo) => (photo.upload.status === "uploaded" ? [photo.upload.photoId] : []));

  return {
    photos,
    uploadedIds,
    uploadingCount: photos.filter((photo) => photo.upload.status === "uploading").length,
    failedCount: photos.filter((photo) => photo.upload.status === "failed").length,
    issue,
    add,
    remove,
    makeCover,
    retry,
  };
}
