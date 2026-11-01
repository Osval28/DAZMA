import Link from "next/link";
import { useTranslations } from "next-intl";
import { SiteHeader } from "@/components/account/SiteHeader";

export default function PublicationNotFound() {
  const t = useTranslations("Publications.detail");

  return (
    <>
      <SiteHeader />
      <main className="detail detail--empty">
        <h1 className="page__title">{t("notFoundTitle")}</h1>
        <p className="page__status">{t("notFoundText")}</p>
        <Link href="/" className="button button--secondary detail__back-button">
          {t("back")}
        </Link>
      </main>
    </>
  );
}
