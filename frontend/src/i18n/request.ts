import { cookies, headers } from "next/headers";
import { getRequestConfig } from "next-intl/server";
import { DEFAULT_LOCALE, LOCALE_COOKIE, isLocale, type AppLocale } from "./config";

function localeFromAcceptLanguage(header: string | null): AppLocale | undefined {
  if (header === null) {
    return undefined;
  }
  for (const part of header.split(",")) {
    const language = part.split(";")[0].trim().toLowerCase().split("-")[0];
    if (isLocale(language)) {
      return language;
    }
  }
  return undefined;
}

export default getRequestConfig(async () => {
  const savedLocale = (await cookies()).get(LOCALE_COOKIE)?.value;
  const locale = isLocale(savedLocale)
    ? savedLocale
    : (localeFromAcceptLanguage((await headers()).get("accept-language")) ?? DEFAULT_LOCALE);

  return {
    locale,
    messages: (await import(`../../messages/${locale}.json`)).default,
  };
});
