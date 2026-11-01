import Link from "next/link";
import { useTranslations } from "next-intl";
import { listingHref, type ListingQuery } from "@/lib/publications/listing-query";

type PageNavigationProps = {
  query: ListingQuery;
  hasNext: boolean;
};

type PageStepProps = {
  label: string;
  href?: string;
};

function PageStep({ label, href }: PageStepProps) {
  if (href === undefined) {
    return (
      <span className="button button--secondary pagination__step pagination__step--disabled" aria-disabled="true">
        {label}
      </span>
    );
  }
  return (
    <Link href={href} className="button button--secondary pagination__step">
      {label}
    </Link>
  );
}

export function PageNavigation({ query, hasNext }: PageNavigationProps) {
  const t = useTranslations("Publications.pagination");
  const hasPrevious = query.page > 0;

  if (!hasPrevious && !hasNext) {
    return null;
  }

  return (
    <nav className="pagination" aria-label={t("label")}>
      <PageStep label={t("previous")} href={hasPrevious ? listingHref({ ...query, page: query.page - 1 }) : undefined} />
      <span className="pagination__current">{t("current", { page: query.page + 1 })}</span>
      <PageStep label={t("next")} href={hasNext ? listingHref({ ...query, page: query.page + 1 }) : undefined} />
    </nav>
  );
}
