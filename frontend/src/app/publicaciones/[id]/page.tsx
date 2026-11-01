import Link from "next/link";
import { notFound } from "next/navigation";
import { getLocale, getTranslations } from "next-intl/server";
import { Avatar } from "@/components/account/Avatar";
import { SiteHeader } from "@/components/account/SiteHeader";
import { Button } from "@/components/ui/Button";
import { PublicationGallery } from "@/components/publications/PublicationGallery";
import { ApiError } from "@/lib/api/errors";
import { listMunicipalities } from "@/lib/api/municipalities";
import { getPublication, type PublicationDetail } from "@/lib/api/publications";
import { formatDate } from "@/lib/publications/format-date";
import { formatPrice } from "@/lib/publications/format-price";

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
  const sellerName = publication.ownerName || undefined;
  const locale = await getLocale();
  const t = await getTranslations("Publications");

  return (
    <>
      <SiteHeader />
      <main id="contenido" className="detail">
        <Link href="/" className="detail__back">
          {t("detail.back")}
        </Link>
        <div className="detail__layout">
          {photos.length > 0 && <PublicationGallery photos={photos} title={publication.name} />}
          <section className="detail__info">
            <span className={`badge badge--${publication.type.toLowerCase()}`}>{t(`types.${publication.type}`)}</span>
            <h1 className="detail__title">{publication.name}</h1>
            <div className="detail__price">
              {isExchange && <span className="detail__price-label">{t("detail.referenceValue")}</span>}
              <p className="detail__price-value">{formatPrice(publication.price, locale)}</p>
            </div>
            <div className="seller">
              <Avatar name={sellerName} size="medium" tinted />
              <div className="seller__text">
                <span className="seller__label">{t("detail.seller")}</span>
                <span className="seller__name">{sellerName ?? t("detail.sellerNotFound")}</span>
              </div>
            </div>
            <dl className="detail__facts">
              <div>
                <dt>{t("detail.location")}</dt>
                <dd>{municipalityName}</dd>
              </div>
              <div>
                <dt>{t("detail.published")}</dt>
                <dd>{formatDate(publication.createdAt, locale)}</dd>
              </div>
            </dl>
            <div className="detail__description">
              <h2 className="detail__subtitle">{t("detail.description")}</h2>
              <p>{publication.description}</p>
            </div>
            <div className="detail__contact">
              <Button variant="secondary" disabled>
                {t("detail.contact")}
              </Button>
              <span className="detail__contact-note">{t("detail.contactSoon")}</span>
            </div>
          </section>
        </div>
      </main>
    </>
  );
}
