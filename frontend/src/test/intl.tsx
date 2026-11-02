import { render } from "@testing-library/react";
import { NextIntlClientProvider, createTranslator } from "next-intl";
import type { ReactElement } from "react";
import english from "../../messages/en.json";
import spanish from "../../messages/es.json";

const catalogs = { es: spanish, en: english };

export type TestLocale = keyof typeof catalogs;

export function renderWithIntl(ui: ReactElement, locale: TestLocale = "es") {
  return render(
    <NextIntlClientProvider locale={locale} messages={catalogs[locale]}>
      {ui}
    </NextIntlClientProvider>,
  );
}

export function translatorFor(locale: TestLocale = "es") {
  return createTranslator({ locale, messages: catalogs[locale] });
}

export const t = translatorFor();
