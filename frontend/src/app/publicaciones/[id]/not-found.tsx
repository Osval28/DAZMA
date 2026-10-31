import Link from "next/link";
import { SiteHeader } from "@/components/account/SiteHeader";
import { publicationMessages } from "@/lib/publications/messages";

const messages = publicationMessages.detail;

export default function PublicationNotFound() {
  return (
    <>
      <SiteHeader />
      <main className="detail detail--empty">
        <h1 className="page__title">{messages.notFoundTitle}</h1>
        <p className="page__status">{messages.notFoundText}</p>
        <Link href="/" className="button button--secondary detail__back-button">
          {messages.back}
        </Link>
      </main>
    </>
  );
}
