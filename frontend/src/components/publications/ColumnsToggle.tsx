"use client";

import { publicationMessages } from "@/lib/publications/messages";

const messages = publicationMessages.view;

type ColumnsToggleProps = {
  options: readonly number[];
  selected: number;
  onSelect: (columns: number) => void;
};

export function ColumnsToggle({ options, selected, onSelect }: ColumnsToggleProps) {
  return (
    <div className="columns-toggle" role="group" aria-label={messages.columnsLabel}>
      <span className="columns-toggle__label" aria-hidden="true">
        {messages.columnsLabel}
      </span>
      {options.map((columns) => (
        <button
          key={columns}
          type="button"
          className="columns-toggle__option"
          aria-pressed={columns === selected}
          aria-label={messages.columnsOption(columns)}
          onClick={() => onSelect(columns)}
        >
          {columns}
        </button>
      ))}
    </div>
  );
}
