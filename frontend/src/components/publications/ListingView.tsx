"use client";

import type { CSSProperties, ReactNode } from "react";
import { DESKTOP_COLUMNS } from "@/lib/publications/view-columns";
import { ColumnsToggle } from "./ColumnsToggle";
import { useStoredColumns } from "./useStoredColumns";

type ListingViewProps = {
  filter: ReactNode;
  children: ReactNode;
};

export function ListingView({ filter, children }: ListingViewProps) {
  const [columns, setColumns] = useStoredColumns(DESKTOP_COLUMNS);
  const columnVariables = { "--columns": columns } as CSSProperties;

  return (
    <div className="listing__view" style={columnVariables}>
      <div className="listing__toolbar">
        {filter}
        <ColumnsToggle options={DESKTOP_COLUMNS.options} selected={columns} onSelect={setColumns} />
      </div>
      {children}
    </div>
  );
}
