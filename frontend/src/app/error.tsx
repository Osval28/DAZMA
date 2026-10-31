"use client";

import { publicationMessages } from "@/lib/publications/messages";
import { Button } from "@/components/ui/Button";

const messages = publicationMessages.error;

export default function ListingError({ reset }: { error: Error; reset: () => void }) {
  return (
    <main className="page">
      <h1 className="page__title">{messages.title}</h1>
      <Button onClick={reset}>{messages.retry}</Button>
    </main>
  );
}
