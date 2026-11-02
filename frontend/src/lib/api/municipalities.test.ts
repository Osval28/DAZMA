import { describe, expect, it, vi } from "vitest";

vi.mock("./http", () => ({ apiJson: vi.fn() }));

import { apiJson } from "./http";
import { listMunicipalities } from "./municipalities";

describe("municipalities API", () => {
  it("lists the municipalities", async () => {
    vi.mocked(apiJson).mockResolvedValue([]);

    await listMunicipalities();

    expect(apiJson).toHaveBeenCalledWith("/shared/api/v1/municipalities");
  });
});
