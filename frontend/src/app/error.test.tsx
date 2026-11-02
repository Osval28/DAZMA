import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header /> }));

import ListingError from "./(listing)/error";
import GenericError from "./error";

describe("error pages", () => {
  it("explains a generic failure and retries", async () => {
    const reset = vi.fn();
    renderWithIntl(<GenericError error={new Error("boom")} reset={reset} />);

    expect(screen.getByRole("heading", { name: t("Errors.generic.title") })).toBeInTheDocument();
    expect(screen.getByText(t("Errors.generic.text"))).toBeInTheDocument();

    await userEvent.click(screen.getByRole("button", { name: t("Errors.retry") }));

    expect(reset).toHaveBeenCalledTimes(1);
  });

  it("explains that the listing could not be loaded and retries", async () => {
    const reset = vi.fn();
    renderWithIntl(<ListingError error={new Error("boom")} reset={reset} />);

    expect(screen.getByRole("heading", { name: t("Errors.listing.title") })).toBeInTheDocument();
    expect(screen.getByText(t("Errors.listing.text"))).toBeInTheDocument();

    await userEvent.click(screen.getByRole("button", { name: t("Errors.retry") }));

    expect(reset).toHaveBeenCalledTimes(1);
  });
});
