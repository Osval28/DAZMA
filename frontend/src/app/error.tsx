"use client";

import { errorMessages } from "@/lib/errors/messages";
import { ErrorScreen } from "@/components/errors/ErrorScreen";

const messages = errorMessages.generic;

export default function GenericError({ reset }: { error: Error; reset: () => void }) {
  return <ErrorScreen title={messages.title} text={messages.text} onRetry={reset} />;
}
