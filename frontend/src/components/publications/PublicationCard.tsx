import Image from "next/image";
import Link from "next/link";
import { useLocale, useTranslations } from "next-intl";
import type { PublicationSummary } from "@/lib/api/publications";
import { formatPrice } from "@/lib/publications/format-price";

type PublicationCardProps = {
  publication: PublicationSummary;
  municipalityName: string;
};

export function PublicationCard({ publication, municipalityName }: PublicationCardProps) {
  const t = useTranslations("Publications.types");
  const locale = useLocale();

  return (
    <article className="publication-card">
      <Link href={`/publicaciones/${publication.id}`} className="publication-card__link">
        <div className="publication-card__media">
          <Image src={publication.coverUrl} alt={`${publication.name}`} fill sizes="(max-width: 40rem) 8rem, 16rem" unoptimized />
        </div>
        <div className="publication-card__body">
          <span className={`badge badge--${publication.type.toLowerCase()}`}>{t(publication.type)}</span>
          <h2 className="publication-card__title">{publication.name}</h2>
          <p className="publication-card__price">{formatPrice(publication.price, locale)}</p>
          <p className="publication-card__meta">{municipalityName}</p>
        </div>
      </Link>
    </article>
  );
}
