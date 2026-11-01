import Link from "next/link";
import type { PublicationSummary } from "@/lib/api/publications";
import { publicationMessages } from "@/lib/publications/messages";
import { NEW_PUBLICATION_PATH } from "@/lib/publications/paths";
import { PublicationCard } from "./PublicationCard";

type PublicationGridProps = {
  publications: PublicationSummary[];
  municipalityNames: Record<string, string>;
};

export function PublicationGrid({ publications, municipalityNames }: PublicationGridProps) {
  if (publications.length === 0) {
    return (
      <div className="listing__empty">
        <p>{publicationMessages.listing.empty}</p>
        <Link href={NEW_PUBLICATION_PATH} className="button button--accent listing__empty-action">
          {publicationMessages.listing.emptyAction}
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
