import { formattingLocaleOf, type AppLocale } from "@/i18n/config";

export function formatPrice(price: number, locale: AppLocale): string {
  return new Intl.NumberFormat(formattingLocaleOf(locale), {
    style: "currency",
    currency: "COP",
    maximumFractionDigits: 0,
  }).format(price);
}
