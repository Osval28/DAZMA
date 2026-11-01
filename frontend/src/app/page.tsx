import { SiteHeader } from "@/components/account/SiteHeader";
import { IconBackdrop } from "@/components/brand/IconBackdrop";
import { ListingView } from "@/components/publications/ListingView";
import { MunicipalityFilter } from "@/components/publications/MunicipalityFilter";
import { PageNavigation } from "@/components/publications/PageNavigation";
import { PublicationGrid } from "@/components/publications/PublicationGrid";
import { listMunicipalities } from "@/lib/api/municipalities";
import { listPublications } from "@/lib/api/publications";
import { parseListingQuery, type RawListingQuery } from "@/lib/publications/listing-query";

type HomePageProps = {
  searchParams: Promise<RawListingQuery>;
};

export default async function HomePage({ searchParams }: HomePageProps) {
  const query = parseListingQuery(await searchParams);
  const [municipalities, publications] = await Promise.all([listMunicipalities(), listPublications(query)]);
  const municipalityNames = Object.fromEntries(municipalities.map((municipality) => [municipality.code, municipality.name]));

  return (
    <>
      <IconBackdrop tone="subtle" />
      <SiteHeader />
      <main id="contenido" className="listing">
        <ListingView filter={<MunicipalityFilter municipalities={municipalities} selected={query.municipality} />}>
          <PublicationGrid publications={publications.items} municipalityNames={municipalityNames} />
        </ListingView>
        <PageNavigation query={query} hasNext={publications.hasNext} />
      </main>
    </>
  );
}
