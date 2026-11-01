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
  ownerName: string;
  type: PublicationType;
  description: string;
  municipality: string;
  price: number;
  status: string;
  photos: PublicationPhoto[];
  createdAt: string;
};

export type CreatePublicationInput = {
  name: string;
  type: PublicationType;
  description: string;
  municipality: string;
  price: number;
  photoUploadIds: string[];
};

export type CreatedPublication = {
  id: string;
  status: string;
  createdAt: string;
};

export type PhotoUploadAuthorization = {
  photoId: string;
  uploadUrl: string;
  expiresAt: string;
};

const PUBLICATIONS_PATH = "/publications/api/v1/publications";
const PHOTO_AUTHORIZATIONS_PATH = "/publications/api/v1/photos/upload-authorizations";

export function createPublication(input: CreatePublicationInput): Promise<CreatedPublication> {
  return apiJson<CreatedPublication>(PUBLICATIONS_PATH, { method: "POST", body: input });
}

export function authorizePhotoUpload(contentType: string, contentLength: number): Promise<PhotoUploadAuthorization> {
  return apiJson<PhotoUploadAuthorization>(PHOTO_AUTHORIZATIONS_PATH, { method: "POST", body: { contentType, contentLength } });
}

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
