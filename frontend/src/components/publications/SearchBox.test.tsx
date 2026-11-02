import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { SearchBox } from "./SearchBox";

describe("SearchBox", () => {
  it("is a labelled search field that is not available yet", () => {
    renderWithIntl(<SearchBox />);

    const input = screen.getByLabelText(t("Publications.search.label"));
    expect(input).toHaveAttribute("type", "search");
    expect(input).toHaveAttribute("placeholder", t("Publications.search.placeholder"));
    expect(input).toBeDisabled();
  });
});
