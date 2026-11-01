"use client";

import Image from "next/image";
import { useRef } from "react";
import type { ChangeEvent } from "react";
import { publicationMessages } from "@/lib/publications/messages";
import { ALLOWED_PHOTO_TYPES, MAXIMUM_PHOTOS, MAXIMUM_PHOTO_MEGABYTES } from "@/lib/publications/photo-upload";
import { Button } from "@/components/ui/Button";
import type { PhotoDrafts } from "./usePhotoDrafts";

const messages = publicationMessages.create.photos;

type PhotoPickerProps = {
  drafts: PhotoDrafts;
  disabled: boolean;
};

export function PhotoPicker({ drafts, disabled }: PhotoPickerProps) {
  const input = useRef<HTMLInputElement>(null);
  const { photos } = drafts;
  const full = photos.length >= MAXIMUM_PHOTOS;

  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    drafts.add(Array.from(event.target.files ?? []));
    event.target.value = "";
  }

  return (
    <div className="photo-picker">
      <div className="photo-picker__header">
        <p className="photo-picker__hint">{messages.hint(MAXIMUM_PHOTOS, MAXIMUM_PHOTO_MEGABYTES)}</p>
        <span className="photo-picker__counter">{messages.counter(photos.length, MAXIMUM_PHOTOS)}</span>
      </div>
      {drafts.issueMessage !== undefined && (
        <p className="photo-picker__issue" role="alert">
          {drafts.issueMessage}
        </p>
      )}
      <ul className="photo-picker__list">
        {photos.map((photo, index) => (
          <li key={photo.key} className="photo-tile">
            <div className="photo-tile__media">
              <Image src={photo.previewUrl} alt={messages.previewAlt(index + 1)} fill unoptimized sizes="10rem" />
              {index === 0 && <span className="photo-tile__cover">{messages.cover}</span>}
              {photo.upload.status === "uploading" && <span className="photo-tile__status">{messages.uploading}</span>}
              {photo.upload.status === "failed" && <span className="photo-tile__status photo-tile__status--failed">{messages.failed}</span>}
            </div>
            <div className="photo-tile__actions">
              {photo.upload.status === "failed" && (
                <button type="button" className="photo-tile__action" onClick={() => drafts.retry(photo.key)} disabled={disabled}>
                  {messages.retry}
                </button>
              )}
              {index > 0 && photo.upload.status === "uploaded" && (
                <button type="button" className="photo-tile__action" onClick={() => drafts.makeCover(photo.key)} disabled={disabled}>
                  {messages.makeCover}
                </button>
              )}
              <button type="button" className="photo-tile__action" onClick={() => drafts.remove(photo.key)} disabled={disabled}>
                {messages.remove}
              </button>
            </div>
          </li>
        ))}
      </ul>
      <input ref={input} type="file" accept={ALLOWED_PHOTO_TYPES.join(",")} multiple hidden onChange={handleChange} />
      <Button variant="secondary" onClick={() => input.current?.click()} disabled={disabled || full}>
        {messages.add}
      </Button>
    </div>
  );
}
