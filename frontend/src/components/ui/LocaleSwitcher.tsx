"use client";

import { useLocale, useTranslations } from "next-intl";
import { useTransition } from "react";
import { setLocale } from "@/i18n/actions";
import { nextLocale } from "@/i18n/config";

export function LocaleSwitcher() {
  const t = useTranslations("Locale");
  const locale = useLocale();
  const [pending, startTransition] = useTransition();
  const target = nextLocale(locale);
  const label = t("switchTo", { language: t(`languages.${target}`) });

  function handleClick() {
    startTransition(async () => {
      await setLocale(target);
    });
  }

  return (
    <button type="button" className="link locale-switcher" onClick={handleClick} disabled={pending} lang={target} aria-label={label} title={label}>
      {target.toUpperCase()}
    </button>
  );
}
