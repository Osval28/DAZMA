import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const mocks = vi.hoisted(() => {
  const push = vi.fn();
  return { push, router: { push } };
});

vi.mock("next/navigation", () => ({ useRouter: () => mocks.router }));

import { MunicipalityFilter } from "./MunicipalityFilter";

const municipalities = [
  { code: "05615", name: "Rionegro", department: "Antioquia" },
  { code: "05001", name: "Medellín", department: "Antioquia" },
];

describe("MunicipalityFilter", () => {
  beforeEach(() => {
    mocks.push.mockReset();
  });

  it("lists all municipalities with an option for every one", () => {
    renderWithIntl(<MunicipalityFilter municipalities={municipalities} />);

    expect(screen.getByLabelText(t("Publications.filter.municipalityLabel"))).toHaveValue("");
    expect(screen.getByRole("option", { name: t("Publications.filter.allMunicipalities") })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "Rionegro" })).toBeInTheDocument();
  });

  it("shows the selected municipality", () => {
    renderWithIntl(<MunicipalityFilter municipalities={municipalities} selected="05615" />);

    expect(screen.getByLabelText(t("Publications.filter.municipalityLabel"))).toHaveValue("05615");
  });

  it("filters by the chosen municipality starting from the first page", async () => {
    renderWithIntl(<MunicipalityFilter municipalities={municipalities} />);

    await userEvent.selectOptions(screen.getByLabelText(t("Publications.filter.municipalityLabel")), "05615");

    expect(mocks.push).toHaveBeenCalledWith("/?municipality=05615");
  });

  it("removes the filter when all municipalities are chosen", async () => {
    renderWithIntl(<MunicipalityFilter municipalities={municipalities} selected="05615" />);

    await userEvent.selectOptions(screen.getByLabelText(t("Publications.filter.municipalityLabel")), "");

    expect(mocks.push).toHaveBeenCalledWith("/");
  });
});
