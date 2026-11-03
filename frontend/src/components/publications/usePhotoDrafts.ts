"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import type { ErrorMessageKey } from "@/i18n/message-keys";
import type { PublicationRules } from "@/lib/api/publications";
import { publicationErrorKey } from "@/lib/publications/errors";
import { uploadPhoto } from "@/lib/publications/photo-upload";

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
  issue: ErrorMessageKey | undefined;
  add: (files: File[]) => void;
  remove: (key: string) => void;
  makeCover: (key: string) => void;
  retry: (key: string) => void;
};

export function usePhotoDrafts(rules?: PublicationRules): PhotoDrafts {
  const [photos, setPhotos] = useState<DraftPhoto[]>([]);
  const [issue, setIssue] = useState<ErrorMessageKey>();
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
        .catch((error) => {
          const reason = publicationErrorKey(error);
          updateUpload(key, { status: "failed", reason });
          setIssue(reason);
        });
    },
    [updateUpload],
  );

  const add = useCallback(
    (files: File[]) => {
      setIssue(undefined);
      const remaining = rules === undefined ? files.length : Math.max(0, rules.maximumPhotos - latestPhotos.current.length);
      const allowed = files.slice(0, remaining).filter((file) => {
        if (rules === undefined) return true;
        if (!rules.allowedPhotoContentTypes.includes(file.type)) {
          setIssue("Publications.create.errors.photoTypeInvalid");
          return false;
        }
        if (file.size > rules.maximumPhotoBytes) {
          setIssue("Publications.create.errors.photoTooLarge");
          return false;
        }
        return true;
      });
      if (files.length > remaining) setIssue("Publications.create.errors.photosLimitReached");
      const accepted: DraftPhoto[] = [];
      for (const file of allowed) {
        accepted.push({ key: crypto.randomUUID(), file, previewUrl: URL.createObjectURL(file), upload: { status: "uploading" } });
      }
      latestPhotos.current = [...latestPhotos.current, ...accepted];
      setPhotos((current) => [...current, ...accepted]);
      accepted.forEach((photo) => startUpload(photo.key, photo.file));
    },
    [rules, startUpload],
  );

  const remove = useCallback((key: string) => {
    setIssue(undefined);
    latestPhotos.current.filter((photo) => photo.key === key).forEach((photo) => URL.revokeObjectURL(photo.previewUrl));
    latestPhotos.current = latestPhotos.current.filter((photo) => photo.key !== key);
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
      setIssue(undefined);
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
