"use client";

import Image from "next/image";
import { useState } from "react";
import type { PublicationPhoto } from "@/lib/api/publications";
import { publicationMessages } from "@/lib/publications/messages";

const messages = publicationMessages.detail;

type PublicationGalleryProps = {
  photos: PublicationPhoto[];
  title: string;
};

export function PublicationGallery({ photos, title }: PublicationGalleryProps) {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const selected = photos[selectedIndex];

  return (
    <div className="gallery" role="group" aria-label={messages.galleryLabel}>
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
                aria-label={messages.thumbnailLabel(index + 1)}
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
