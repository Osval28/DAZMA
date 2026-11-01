export const NEW_PUBLICATION_PATH = "/publicaciones/nueva";

export function publicationPath(id: string): string {
  return `/publicaciones/${encodeURIComponent(id)}`;
}
