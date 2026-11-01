export type ColumnsPreference = {
  storageKey: string;
  options: readonly number[];
  defaultColumns: number;
};

export const DESKTOP_COLUMNS: ColumnsPreference = {
  storageKey: "dazma.listing.columns.desktop",
  options: [2, 3, 4, 5],
  defaultColumns: 4,
};
