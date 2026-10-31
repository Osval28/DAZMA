"use client";

import type { CSSProperties, ReactNode } from "react";
import { DESKTOP_COLUMNS, MOBILE_COLUMNS } from "@/lib/publications/view-columns";
import { ColumnsToggle } from "./ColumnsToggle";
import { useStoredColumns } from "./useStoredColumns";

type ListingViewProps = {
  filter: ReactNode;
  children: ReactNode;
};

export function ListingView({ filter, children }: ListingViewProps) {
  const [desktopColumns, setDesktopColumns] = useStoredColumns(DESKTOP_COLUMNS);
  const [mobileColumns, setMobileColumns] = useStoredColumns(MOBILE_COLUMNS);
  const columnVariables = { "--columns": desktopColumns, "--columns-mobile": mobileColumns } as CSSProperties;

  return (
    <div className="listing__view" style={columnVariables}>
      <div className="listing__toolbar">
        {filter}
        <ColumnsToggle variant="desktop" options={DESKTOP_COLUMNS.options} selected={desktopColumns} onSelect={setDesktopColumns} />
        <ColumnsToggle variant="mobile" options={MOBILE_COLUMNS.options} selected={mobileColumns} onSelect={setMobileColumns} />
      </div>
      {children}
    </div>
  );
}
