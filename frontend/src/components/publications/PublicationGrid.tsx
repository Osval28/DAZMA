import type { PublicationSummary } from "@/lib/api/publications";
import { publicationMessages } from "@/lib/publications/messages";
import { PublicationCard } from "./PublicationCard";

type PublicationGridProps = {
  publications: PublicationSummary[];
  municipalityNames: Record<string, string>;
};

export function PublicationGrid({ publications, municipalityNames }: PublicationGridProps) {
  if (publications.length === 0) {
    return <p className="listing__empty">{publicationMessages.listing.empty}</p>;
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
