"use client";

import Image from "next/image";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { PublicationPhoto } from "@/lib/api/publications";

type PublicationGalleryProps = {
  photos: PublicationPhoto[];
  title: string;
};

export function PublicationGallery({ photos, title }: PublicationGalleryProps) {
  const t = useTranslations("Publications.detail");
  const [selectedIndex, setSelectedIndex] = useState(0);
  const selected = photos[selectedIndex];

  return (
    <div className="gallery" role="group" aria-label={t("galleryLabel")}>
      <div className="gallery__main">
        <Image src={selected.url} alt={title} fill sizes="(max-width: 56rem) 100vw, 40rem" unoptimized priority />
      </div>
      {photos.length > 1 && (
        <ul className="gallery__thumbnails">
          {photos.map((photo, index) => (
            <li key={photo.id}>
              <button
                type="button"
                className="gallery__thumbnail"
                aria-pressed={index === selectedIndex}
                aria-label={t("thumbnailLabel", { position: index + 1 })}
                onClick={() => setSelectedIndex(index)}
              >
                <Image src={photo.url} alt="" fill sizes="5rem" unoptimized />
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
