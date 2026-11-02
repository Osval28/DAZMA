import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { ColumnsToggle } from "./ColumnsToggle";

describe("ColumnsToggle", () => {
  it("offers every option and marks the selected one", () => {
    renderWithIntl(<ColumnsToggle options={[2, 3, 4]} selected={3} onSelect={vi.fn()} />);

    expect(screen.getByRole("group", { name: t("Publications.view.columnsLabel") })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: t("Publications.view.columnsOption", { columns: 3 }) })).toHaveAttribute("aria-pressed", "true");
    expect(screen.getByRole("button", { name: t("Publications.view.columnsOption", { columns: 2 }) })).toHaveAttribute("aria-pressed", "false");
  });

  it("reports the chosen number of columns", async () => {
    const onSelect = vi.fn();
    renderWithIntl(<ColumnsToggle options={[2, 3, 4]} selected={3} onSelect={onSelect} />);

    await userEvent.click(screen.getByRole("button", { name: t("Publications.view.columnsOption", { columns: 4 }) }));

    expect(onSelect).toHaveBeenCalledWith(4);
  });
});
