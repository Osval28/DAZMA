import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import { NewPublicationView } from "@/components/publications/NewPublicationView";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Metadata.pages");
  return { title: t("newPublication") };
}

export default function NewPublicationPage() {
  return <NewPublicationView />;
}
