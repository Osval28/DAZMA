import { renderHook, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

const { listMunicipalities } = vi.hoisted(() => ({ listMunicipalities: vi.fn() }));

vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities }));

import { useMunicipalityName } from "./useMunicipalityName";

describe("useMunicipalityName", () => {
  beforeEach(() => {
    listMunicipalities.mockReset();
  });

  it("shows the code until the names arrive", () => {
    listMunicipalities.mockReturnValue(new Promise(() => undefined));

    const { result } = renderHook(() => useMunicipalityName("05615"));

    expect(result.current).toBe("05615");
  });

  it("resolves the code to the municipality name", async () => {
    listMunicipalities.mockResolvedValue([{ code: "05615", name: "Rionegro", department: "Antioquia" }]);

    const { result } = renderHook(() => useMunicipalityName("05615"));

    await waitFor(() => expect(result.current).toBe("Rionegro"));
  });

  it("keeps the code when the municipality is unknown", async () => {
    listMunicipalities.mockResolvedValue([{ code: "05001", name: "Medellín", department: "Antioquia" }]);

    const { result } = renderHook(() => useMunicipalityName("05615"));

    await waitFor(() => expect(listMunicipalities).toHaveBeenCalled());
    expect(result.current).toBe("05615");
  });

  it("keeps the code when the municipalities cannot be loaded", async () => {
    listMunicipalities.mockRejectedValue(new Error("offline"));

    const { result } = renderHook(() => useMunicipalityName("05615"));

    await waitFor(() => expect(listMunicipalities).toHaveBeenCalled());
    expect(result.current).toBe("05615");
  });
});
