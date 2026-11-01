"use client";

import { errorMessages } from "@/lib/errors/messages";
import { ErrorScreen } from "@/components/errors/ErrorScreen";

const messages = errorMessages.listing;

export default function ListingError({ reset }: { error: Error; reset: () => void }) {
  return <ErrorScreen title={messages.title} text={messages.text} onRetry={reset} />;
}
