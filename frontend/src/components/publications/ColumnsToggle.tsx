"use client";

import { useTranslations } from "next-intl";

type ColumnsToggleProps = {
  options: readonly number[];
  selected: number;
  onSelect: (columns: number) => void;
};

export function ColumnsToggle({ options, selected, onSelect }: ColumnsToggleProps) {
  const t = useTranslations("Publications.view");

  return (
    <div className="columns-toggle" role="group" aria-label={t("columnsLabel")}>
      <span className="columns-toggle__label" aria-hidden="true">
        {t("columnsLabel")}
      </span>
      {options.map((columns) => (
        <button
          key={columns}
          type="button"
          className="columns-toggle__option"
          aria-pressed={columns === selected}
          aria-label={t("columnsOption", { columns })}
          onClick={() => onSelect(columns)}
        >
          {columns}
        </button>
      ))}
    </div>
  );
}
