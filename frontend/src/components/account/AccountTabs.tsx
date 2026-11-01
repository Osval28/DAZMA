"use client";

import { useRef, useState } from "react";
import type { KeyboardEvent, ReactNode } from "react";

export type AccountTab = {
  id: string;
  label: string;
  content: ReactNode;
};

type AccountTabsProps = {
  label: string;
  tabs: AccountTab[];
};

export function AccountTabs({ label, tabs }: AccountTabsProps) {
  const [selectedId, setSelectedId] = useState(tabs[0].id);
  const buttons = useRef<Record<string, HTMLButtonElement | null>>({});
  const selected = tabs.find((tab) => tab.id === selectedId) ?? tabs[0];

  function handleKeyDown(event: KeyboardEvent<HTMLButtonElement>, index: number) {
    const offset = event.key === "ArrowRight" ? 1 : event.key === "ArrowLeft" ? -1 : 0;
    if (offset === 0) {
      return;
    }
    event.preventDefault();
    const next = tabs[(index + offset + tabs.length) % tabs.length];
    setSelectedId(next.id);
    buttons.current[next.id]?.focus();
  }

  return (
    <div className="tabs">
      <div className="tabs__list" role="tablist" aria-label={label}>
        {tabs.map((tab, index) => (
          <button
            key={tab.id}
            ref={(element) => {
              buttons.current[tab.id] = element;
            }}
            id={`account-tab-${tab.id}`}
            type="button"
            role="tab"
            className="tabs__tab"
            aria-selected={tab.id === selected.id}
            aria-controls={`account-panel-${tab.id}`}
            tabIndex={tab.id === selected.id ? 0 : -1}
            onClick={() => setSelectedId(tab.id)}
            onKeyDown={(event) => handleKeyDown(event, index)}
          >
            {tab.label}
          </button>
        ))}
      </div>
      <div id={`account-panel-${selected.id}`} role="tabpanel" aria-labelledby={`account-tab-${selected.id}`} className="tabs__panel">
        {selected.content}
      </div>
    </div>
  );
}
