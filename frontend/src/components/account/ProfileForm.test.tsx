import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "@/lib/api/errors";
import { renderWithIntl, t } from "@/test/intl";

const { replace, registerUser, getPhoneRules, listMunicipalities } = vi.hoisted(() => ({
  replace: vi.fn(),
  registerUser: vi.fn(),
  getPhoneRules: vi.fn(),
  listMunicipalities: vi.fn(),
}));

vi.mock("next/navigation", () => ({ useRouter: () => ({ replace }) }));
vi.mock("@/lib/api/users", () => ({ registerUser, getPhoneRules }));
vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities }));

import { ProfileForm } from "./ProfileForm";

async function fillValidProfile() {
  await userEvent.type(screen.getByLabelText(t("Auth.profile.nameLabel")), "Ana");
  await userEvent.type(screen.getByLabelText(t("Auth.profile.phoneLabel")), "300 123 4567");
  await userEvent.selectOptions(await screen.findByLabelText(t("Auth.profile.municipalityLabel")), "05615");
  await screen.findByRole("option", { name: "Rionegro" });
}

describe("ProfileForm", () => {
  beforeEach(() => {
    replace.mockReset();
    registerUser.mockReset();
    getPhoneRules.mockResolvedValue({ countryCode: "57", nationalLength: 10, mobilePrefix: "3" });
    listMunicipalities.mockResolvedValue([{ code: "05615", name: "Rionegro", department: "Antioquia" }]);
  });

  it("rejects an invalid profile without calling the API", async () => {
    renderWithIntl(<ProfileForm />);

    await userEvent.click(screen.getByRole("button", { name: t("Account.completeProfile.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.nameRequired"));
    expect(registerUser).not.toHaveBeenCalled();
  });

  it("registers the profile and goes home", async () => {
    registerUser.mockResolvedValue(undefined);
    renderWithIntl(<ProfileForm />);
    await screen.findByRole("option", { name: "Rionegro" });
    await fillValidProfile();

    await userEvent.click(screen.getByRole("button", { name: t("Account.completeProfile.submit") }));

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/"));
    expect(registerUser).toHaveBeenCalledWith({ name: "Ana", phone: "3001234567", municipality: "05615" });
  });

  it("goes home when the profile already exists", async () => {
    registerUser.mockRejectedValue(new ApiError(409, "USER_ALREADY_REGISTERED"));
    renderWithIntl(<ProfileForm />);
    await screen.findByRole("option", { name: "Rionegro" });
    await fillValidProfile();

    await userEvent.click(screen.getByRole("button", { name: t("Account.completeProfile.submit") }));

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/"));
  });

  it("shows other failures and lets the user try again", async () => {
    registerUser.mockRejectedValue(new ApiError(400, "USER_PHONE_INVALID"));
    renderWithIntl(<ProfileForm />);
    await screen.findByRole("option", { name: "Rionegro" });
    await fillValidProfile();

    await userEvent.click(screen.getByRole("button", { name: t("Account.completeProfile.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.phoneInvalid"));
    expect(screen.getByRole("button", { name: t("Account.completeProfile.submit") })).toBeEnabled();
    expect(replace).not.toHaveBeenCalled();
  });
});
