import { formattingLocaleOf, type AppLocale } from "@/i18n/config";

export function formatDate(isoDate: string, locale: AppLocale): string {
  return new Intl.DateTimeFormat(formattingLocaleOf(locale), {
    day: "numeric",
    month: "long",
    year: "numeric",
  }).format(new Date(isoDate));
}
