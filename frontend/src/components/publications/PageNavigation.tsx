import Link from "next/link";
import { listingHref, type ListingQuery } from "@/lib/publications/listing-query";
import { publicationMessages } from "@/lib/publications/messages";

const messages = publicationMessages.pagination;

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
  const hasPrevious = query.page > 0;

  if (!hasPrevious && !hasNext) {
    return null;
  }

  return (
    <nav className="pagination" aria-label={messages.label}>
      <PageStep label={messages.previous} href={hasPrevious ? listingHref({ ...query, page: query.page - 1 }) : undefined} />
      <span className="pagination__current">{messages.current(query.page + 1)}</span>
      <PageStep label={messages.next} href={hasNext ? listingHref({ ...query, page: query.page + 1 }) : undefined} />
    </nav>
  );
}
