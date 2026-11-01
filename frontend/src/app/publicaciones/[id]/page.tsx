import Link from "next/link";
import { notFound } from "next/navigation";
import { SiteHeader } from "@/components/account/SiteHeader";
import { Button } from "@/components/ui/Button";
import { PublicationGallery } from "@/components/publications/PublicationGallery";
import { ApiError } from "@/lib/api/errors";
import { listMunicipalities } from "@/lib/api/municipalities";
import { getPublication, type PublicationDetail } from "@/lib/api/publications";
import { formatDate } from "@/lib/publications/format-date";
import { formatPrice } from "@/lib/publications/format-price";
import { publicationMessages } from "@/lib/publications/messages";

const messages = publicationMessages.detail;
const NOT_FOUND_STATUSES = [400, 404];

type PublicationPageProps = {
  params: Promise<{ id: string }>;
};

async function loadPublication(id: string): Promise<PublicationDetail> {
  try {
    return await getPublication(id);
  } catch (error) {
    if (error instanceof ApiError && NOT_FOUND_STATUSES.includes(error.status)) {
      notFound();
    }
    throw error;
  }
}

export default async function PublicationPage({ params }: PublicationPageProps) {
  const { id } = await params;
  const [publication, municipalities] = await Promise.all([loadPublication(id), listMunicipalities()]);
  const municipalityName = municipalities.find((municipality) => municipality.code === publication.municipality)?.name ?? publication.municipality;
  const photos = [...publication.photos].sort((first, second) => first.position - second.position);
  const isExchange = publication.type === "EXCHANGE";

  return (
    <>
      <SiteHeader />
      <main id="contenido" className="detail">
        <Link href="/" className="detail__back">
          {messages.back}
        </Link>
        <div className="detail__layout">
          {photos.length > 0 && <PublicationGallery photos={photos} title={publication.name} />}
          <section className="detail__info">
            <span className={`badge badge--${publication.type.toLowerCase()}`}>{publicationMessages.types[publication.type]}</span>
            <h1 className="detail__title">{publication.name}</h1>
            <div className="detail__price">
              {isExchange && <span className="detail__price-label">{messages.referenceValue}</span>}
              <p className="detail__price-value">{formatPrice(publication.price)}</p>
            </div>
            <dl className="detail__facts">
              <div>
                <dt>{messages.location}</dt>
                <dd>{municipalityName}</dd>
              </div>
              <div>
                <dt>{messages.published}</dt>
                <dd>{formatDate(publication.createdAt)}</dd>
              </div>
            </dl>
            <div className="detail__description">
              <h2 className="detail__subtitle">{messages.description}</h2>
              <p>{publication.description}</p>
            </div>
            <div className="detail__contact">
              <Button variant="secondary" disabled>
                {messages.contact}
              </Button>
              <span className="detail__contact-note">{messages.contactSoon}</span>
            </div>
          </section>
        </div>
      </main>
    </>
  );
}
