export const PAGE_SIZE = 20;

export type ListingQuery = {
  municipality?: string;
  page: number;
};

type RawParameter = string | string[] | undefined;

export type RawListingQuery = {
  municipality?: RawParameter;
  page?: RawParameter;
};

const MUNICIPALITY_PATTERN = /^\d{5}$/;
const PAGE_PATTERN = /^\d{1,6}$/;

function firstValue(value: RawParameter): string | undefined {
  return Array.isArray(value) ? value[0] : value;
}

export function parseListingQuery(raw: RawListingQuery): ListingQuery {
  const municipality = firstValue(raw.municipality);
  const page = firstValue(raw.page);
  return {
    municipality: municipality !== undefined && MUNICIPALITY_PATTERN.test(municipality) ? municipality : undefined,
    page: page !== undefined && PAGE_PATTERN.test(page) ? Number(page) : 0,
  };
}

export function listingHref({ municipality, page }: ListingQuery): string {
  const parameters = new URLSearchParams();
  if (municipality !== undefined) {
    parameters.set("municipality", municipality);
  }
  if (page > 0) {
    parameters.set("page", String(page));
  }
  const query = parameters.toString();
  return query.length > 0 ? `/?${query}` : "/";
}
