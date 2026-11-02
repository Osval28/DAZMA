import { screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header /> }));

import PublicationNotFound from "./not-found";

describe("PublicationNotFound", () => {
  it("explains that the publication does not exist and offers a way back", () => {
    renderWithIntl(<PublicationNotFound />);

    expect(screen.getByRole("heading", { name: t("Publications.detail.notFoundTitle") })).toBeInTheDocument();
    expect(screen.getByText(t("Publications.detail.notFoundText"))).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Publications.detail.back") })).toHaveAttribute("href", "/");
  });
});
