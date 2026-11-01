import Link from "next/link";
import { useTranslations } from "next-intl";
import type { PublicationSummary } from "@/lib/api/publications";
import { NEW_PUBLICATION_PATH } from "@/lib/publications/paths";
import { PublicationCard } from "./PublicationCard";

type PublicationGridProps = {
  publications: PublicationSummary[];
  municipalityNames: Record<string, string>;
};

export function PublicationGrid({ publications, municipalityNames }: PublicationGridProps) {
  const t = useTranslations("Publications.listing");

  if (publications.length === 0) {
    return (
      <div className="listing__empty">
        <p>{t("empty")}</p>
        <Link href={NEW_PUBLICATION_PATH} className="button button--accent listing__empty-action">
          {t("emptyAction")}
        </Link>
      </div>
    );
  }

  return (
    <ul className="publication-grid">
      {publications.map((publication) => (
        <li key={publication.id}>
          <PublicationCard publication={publication} municipalityName={municipalityNames[publication.municipality]} />
        </li>
      ))}
    </ul>
  );
}
