import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const { listMunicipalities } = vi.hoisted(() => ({ listMunicipalities: vi.fn() }));

vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities }));

import { MunicipalityField } from "./MunicipalityField";

describe("MunicipalityField", () => {
  beforeEach(() => {
    listMunicipalities.mockReset();
  });

  it("is disabled with a loading placeholder while the municipalities arrive", () => {
    listMunicipalities.mockReturnValue(new Promise(() => undefined));

    renderWithIntl(<MunicipalityField id="municipality" name="municipality" />);

    expect(screen.getByLabelText(t("Auth.profile.municipalityLabel"))).toBeDisabled();
    expect(screen.getByRole("option", { name: t("Auth.profile.municipalitiesLoading") })).toBeInTheDocument();
  });

  it("offers the municipalities once loaded", async () => {
    listMunicipalities.mockResolvedValue([
      { code: "05615", name: "Rionegro", department: "Antioquia" },
      { code: "05001", name: "Medellín", department: "Antioquia" },
    ]);

    renderWithIntl(<MunicipalityField id="municipality" name="municipality" />);

    expect(await screen.findByRole("option", { name: "Rionegro" })).toHaveValue("05615");
    expect(screen.getByLabelText(t("Auth.profile.municipalityLabel"))).toBeEnabled();
    expect(screen.getByRole("option", { name: t("Auth.profile.municipalityPlaceholder") })).toBeInTheDocument();
  });

  it("stays disabled and says so when the municipalities cannot be loaded", async () => {
    listMunicipalities.mockRejectedValue(new Error("offline"));

    renderWithIntl(<MunicipalityField id="municipality" name="municipality" />);

    expect(await screen.findByRole("option", { name: t("Auth.profile.municipalitiesFailed") })).toBeInTheDocument();
    expect(screen.getByLabelText(t("Auth.profile.municipalityLabel"))).toBeDisabled();
  });
});
