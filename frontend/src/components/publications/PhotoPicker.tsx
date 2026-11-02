"use client";

import Image from "next/image";
import { useTranslations } from "next-intl";
import { useRef } from "react";
import type { ChangeEvent } from "react";
import { Button } from "@/components/ui/Button";
import type { PhotoDrafts } from "./usePhotoDrafts";

type PhotoPickerProps = {
  drafts: PhotoDrafts;
  disabled: boolean;
};

export function PhotoPicker({ drafts, disabled }: PhotoPickerProps) {
  const t = useTranslations();
  const input = useRef<HTMLInputElement>(null);
  const { photos } = drafts;

  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    drafts.add(Array.from(event.target.files ?? []));
    event.target.value = "";
  }

  return (
    <div className="photo-picker">
      <div className="photo-picker__header">
        <p className="photo-picker__hint">{t("Publications.create.photos.hint")}</p>
        <span className="photo-picker__counter">{photos.length}</span>
      </div>
      {drafts.issue !== undefined && (
        <p className="photo-picker__issue" role="alert">
          {t(drafts.issue)}
        </p>
      )}
      <ul className="photo-picker__list">
        {photos.map((photo, index) => (
          <li key={photo.key} className="photo-tile">
            <div className="photo-tile__media">
              <Image src={photo.previewUrl} alt={t("Publications.create.photos.previewAlt", { position: index + 1 })} fill unoptimized sizes="10rem" />
              {index === 0 && <span className="photo-tile__cover">{t("Publications.create.photos.cover")}</span>}
              {photo.upload.status === "uploading" && <span className="photo-tile__status">{t("Publications.create.photos.uploading")}</span>}
              {photo.upload.status === "failed" && <span className="photo-tile__status photo-tile__status--failed">{t("Publications.create.photos.failed")}</span>}
            </div>
            <div className="photo-tile__actions">
              {photo.upload.status === "failed" && (
                <button type="button" className="photo-tile__action" onClick={() => drafts.retry(photo.key)} disabled={disabled}>
                  {t("Publications.create.photos.retry")}
                </button>
              )}
              {index > 0 && photo.upload.status === "uploaded" && (
                <button type="button" className="photo-tile__action" onClick={() => drafts.makeCover(photo.key)} disabled={disabled}>
                  {t("Publications.create.photos.makeCover")}
                </button>
              )}
              <button type="button" className="photo-tile__action" onClick={() => drafts.remove(photo.key)} disabled={disabled}>
                {t("Publications.create.photos.remove")}
              </button>
            </div>
          </li>
        ))}
      </ul>
      <input ref={input} type="file" accept="image/*" multiple hidden onChange={handleChange} />
      <Button variant="secondary" onClick={() => input.current?.click()} disabled={disabled}>
        {t("Publications.create.photos.add")}
      </Button>
    </div>
  );
}
