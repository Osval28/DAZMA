import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { ListingView } from "./ListingView";

describe("ListingView", () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it("shows the filter and the content", () => {
    renderWithIntl(
      <ListingView filter={<p>filtro</p>}>
        <p>contenido</p>
      </ListingView>,
    );

    expect(screen.getByText("filtro")).toBeInTheDocument();
    expect(screen.getByText("contenido")).toBeInTheDocument();
  });

  it("lays the content out in the default number of columns", () => {
    const { container } = renderWithIntl(
      <ListingView filter={null}>
        <p>contenido</p>
      </ListingView>,
    );

    expect((container.firstElementChild as HTMLElement).style.getPropertyValue("--columns")).toBe("4");
  });

  it("changes the columns when the user picks another option", async () => {
    const { container } = renderWithIntl(
      <ListingView filter={null}>
        <p>contenido</p>
      </ListingView>,
    );

    await userEvent.click(screen.getByRole("button", { name: t("Publications.view.columnsOption", { columns: 2 }) }));

    expect((container.firstElementChild as HTMLElement).style.getPropertyValue("--columns")).toBe("2");
  });
});
