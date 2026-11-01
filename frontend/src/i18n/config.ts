export const LOCALES = ["es", "en"] as const;

export type AppLocale = (typeof LOCALES)[number];

export const DEFAULT_LOCALE: AppLocale = "es";

export const LOCALE_COOKIE = "NEXT_LOCALE";

const FORMATTING_LOCALES: Record<AppLocale, string> = {
  es: "es-CO",
  en: "en-US",
};

export function isLocale(value: string | undefined): value is AppLocale {
  return value !== undefined && (LOCALES as readonly string[]).includes(value);
}

export function nextLocale(current: AppLocale): AppLocale {
  const index = LOCALES.indexOf(current);
  return LOCALES[(index + 1) % LOCALES.length];
}

export function formattingLocaleOf(locale: AppLocale): string {
  return FORMATTING_LOCALES[locale];
}
