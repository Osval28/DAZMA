export const publicationMessages = {
  types: {
    PURCHASE: "Compra",
    EXCHANGE: "Cambio",
  },
  search: {
    label: "Buscar publicaciones",
    placeholder: "Próximamente",
  },
  filter: {
    municipalityLabel: "Municipio",
    allMunicipalities: "Todos los municipios",
  },
  listing: {
    empty: "Aún no hay publicaciones para mostrar aquí.",
  },
  view: {
    columnsLabel: "Columnas",
    columnsOption: (columns: number) => (columns === 1 ? "1 columna" : `${columns} columnas`),
  },
  pagination: {
    label: "Paginación",
    previous: "Anterior",
    next: "Siguiente página",
    current: (page: number) => `Página ${page}`,
  },
  error: {
    title: "No pudimos cargar las publicaciones",
    retry: "Reintentar",
  },
  detail: {
    back: "Volver a las publicaciones",
    description: "Descripción",
    location: "Municipio",
    published: "Publicado",
    referenceValue: "Valor de referencia",
    contact: "Contactar al vendedor",
    contactSoon: "Próximamente",
    galleryLabel: "Fotografías de la publicación",
    thumbnailLabel: (position: number) => `Ver fotografía ${position}`,
    notFoundTitle: "No encontramos esta publicación",
    notFoundText: "Puede que ya no esté disponible o que el enlace sea incorrecto.",
  },
} as const;
