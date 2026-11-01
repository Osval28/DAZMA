"use client";

import { useTranslations } from "next-intl";
import { ErrorScreen } from "@/components/errors/ErrorScreen";

export default function ListingError({ reset }: { error: Error; reset: () => void }) {
  const t = useTranslations("Errors.listing");
  return <ErrorScreen title={t("title")} text={t("text")} onRetry={reset} />;
}
