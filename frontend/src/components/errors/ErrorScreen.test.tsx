import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header data-testid="site-header" /> }));

import { ErrorScreen } from "./ErrorScreen";

describe("ErrorScreen", () => {
  it("shows the title and the text", () => {
    renderWithIntl(<ErrorScreen title="Algo falló" text="Inténtalo más tarde" onRetry={vi.fn()} />);

    expect(screen.getByRole("heading", { name: "Algo falló" })).toBeInTheDocument();
    expect(screen.getByText("Inténtalo más tarde")).toBeInTheDocument();
    expect(screen.getByTestId("site-header")).toBeInTheDocument();
  });

  it("retries when asked", async () => {
    const onRetry = vi.fn();
    renderWithIntl(<ErrorScreen title="Algo falló" text="Texto" onRetry={onRetry} />);

    await userEvent.click(screen.getByRole("button", { name: t("Errors.retry") }));

    expect(onRetry).toHaveBeenCalledTimes(1);
  });

  it("offers a way back home", () => {
    renderWithIntl(<ErrorScreen title="Algo falló" text="Texto" onRetry={vi.fn()} />);

    expect(screen.getByRole("link", { name: t("Errors.home") })).toHaveAttribute("href", "/");
  });
});
