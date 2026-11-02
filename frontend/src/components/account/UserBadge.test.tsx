import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { UserBadge } from "./UserBadge";

describe("UserBadge", () => {
  it("links to the account showing the name", () => {
    renderWithIntl(<UserBadge name="Ana" />);

    const link = screen.getByRole("link", { name: t("Account.header.accountOf", { name: "Ana" }) });
    expect(link).toHaveAttribute("href", "/cuenta");
    expect(link).toHaveTextContent("Ana");
  });

  it("falls back to a generic label without a name", () => {
    renderWithIntl(<UserBadge name={undefined} />);

    expect(screen.getByRole("link")).toHaveTextContent(t("Account.header.account"));
  });
});
