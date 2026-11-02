import { screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities: vi.fn().mockResolvedValue([]) }));

import { ProfileFields } from "./ProfileFields";

describe("ProfileFields", () => {
  it("renders name, phone and municipality with prefixed ids", async () => {
    renderWithIntl(<ProfileFields idPrefix="register" />);

    expect(screen.getByLabelText(t("Auth.profile.nameLabel"))).toHaveAttribute("id", "register-name");
    expect(screen.getByLabelText(t("Auth.profile.phoneLabel"))).toHaveAttribute("id", "register-phone");
    expect(await screen.findByLabelText(t("Auth.profile.municipalityLabel"))).toHaveAttribute("id", "register-municipality");
  });

  it("names the fields as the forms read them", () => {
    renderWithIntl(<ProfileFields idPrefix="profile" />);

    expect(screen.getByLabelText(t("Auth.profile.nameLabel"))).toHaveAttribute("name", "name");
    expect(screen.getByLabelText(t("Auth.profile.phoneLabel"))).toHaveAttribute("name", "phone");
    expect(screen.getByLabelText(t("Auth.profile.municipalityLabel"))).toHaveAttribute("name", "municipality");
  });
});
