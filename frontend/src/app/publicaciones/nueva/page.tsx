import type { Metadata } from "next";
import { NewPublicationView } from "@/components/publications/NewPublicationView";

export const metadata: Metadata = {
  title: "Publicar | DAZMA",
};

export default function NewPublicationPage() {
  return <NewPublicationView />;
}
