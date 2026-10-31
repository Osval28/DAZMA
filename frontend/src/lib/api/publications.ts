import { PAGE_SIZE, type ListingQuery } from "@/lib/publications/listing-query";
import { apiJson } from "./http";

export type PublicationType = "PURCHASE" | "EXCHANGE";

export type PublicationSummary = {
  id: string;
  name: string;
  type: PublicationType;
  municipality: string;
  price: number;
  coverUrl: string;
  createdAt: string;
};

export type PublicationPage = {
  items: PublicationSummary[];
  page: number;
  size: number;
  hasNext: boolean;
};

export type PublicationPhoto = {
  id: string;
  url: string;
  position: number;
};

export type PublicationDetail = {
  id: string;
  name: string;
  type: PublicationType;
  description: string;
  municipality: string;
  price: number;
  status: string;
  photos: PublicationPhoto[];
  createdAt: string;
};

const PUBLICATIONS_PATH = "/publications/api/v1/publications";

export function getPublication(id: string): Promise<PublicationDetail> {
  return apiJson<PublicationDetail>(`${PUBLICATIONS_PATH}/${encodeURIComponent(id)}`);
}

export function listPublications({ municipality, page }: ListingQuery): Promise<PublicationPage> {
  const parameters = new URLSearchParams({ page: String(page), size: String(PAGE_SIZE) });
  if (municipality !== undefined) {
    parameters.set("municipality", municipality);
  }
  return apiJson<PublicationPage>(`${PUBLICATIONS_PATH}?${parameters}`);
}
