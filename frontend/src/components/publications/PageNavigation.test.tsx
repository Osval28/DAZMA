import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { PageNavigation } from "./PageNavigation";

describe("PageNavigation", () => {
  it("renders nothing when there is a single page", () => {
    const { container } = renderWithIntl(<PageNavigation query={{ page: 0 }} hasNext={false} />);

    expect(container).toBeEmptyDOMElement();
  });

  it("only goes forward from the first page", () => {
    renderWithIntl(<PageNavigation query={{ page: 0 }} hasNext />);

    expect(screen.getByText(t("Publications.pagination.previous"))).toHaveAttribute("aria-disabled", "true");
    expect(screen.getByRole("link", { name: t("Publications.pagination.next") })).toHaveAttribute("href", "/?page=1");
    expect(screen.getByText(t("Publications.pagination.current", { page: 1 }))).toBeInTheDocument();
  });

  it("goes both ways from a middle page keeping the filter", () => {
    renderWithIntl(<PageNavigation query={{ municipality: "05615", page: 2 }} hasNext />);

    expect(screen.getByRole("link", { name: t("Publications.pagination.previous") })).toHaveAttribute("href", "/?municipality=05615&page=1");
    expect(screen.getByRole("link", { name: t("Publications.pagination.next") })).toHaveAttribute("href", "/?municipality=05615&page=3");
    expect(screen.getByText(t("Publications.pagination.current", { page: 3 }))).toBeInTheDocument();
  });

  it("only goes back from the last page", () => {
    renderWithIntl(<PageNavigation query={{ page: 1 }} hasNext={false} />);

    expect(screen.getByRole("link", { name: t("Publications.pagination.previous") })).toHaveAttribute("href", "/");
    expect(screen.getByText(t("Publications.pagination.next"))).toHaveAttribute("aria-disabled", "true");
  });
});
