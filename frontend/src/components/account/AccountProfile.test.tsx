import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { MyProfile } from "@/lib/api/users";
import { renderWithIntl, t } from "@/test/intl";

const { listMunicipalities } = vi.hoisted(() => ({ listMunicipalities: vi.fn() }));

vi.mock("next/navigation", () => ({ useRouter: () => ({ replace: vi.fn() }) }));
vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities }));
vi.mock("@/lib/auth/email-verification", () => ({ refreshVerifiedSession: vi.fn() }));
vi.mock("@/lib/auth/sign-in", () => ({ ACCOUNT_PATH: "/cuenta", LOGIN_PATH: "/ingresar", signOutCompletely: vi.fn() }));

import { AccountProfile } from "./AccountProfile";

const profile: MyProfile = {
  id: "u1",
  name: "Ana",
  phone: "3001234567",
  municipality: "05615",
  state: "ACTIVE",
  role: "USER",
  email: "ana@dazma.store",
  emailVerified: true,
};

describe("AccountProfile", () => {
  beforeEach(() => {
    listMunicipalities.mockResolvedValue([{ code: "05615", name: "Rionegro", department: "Antioquia" }]);
  });

  it("shows the identity of the user and the municipality name", async () => {
    renderWithIntl(<AccountProfile profile={profile} onReload={vi.fn()} />);

    expect(screen.getByRole("heading", { name: "Ana" })).toBeInTheDocument();
    expect(await screen.findByText("Rionegro")).toBeInTheDocument();
  });

  it("shows the contact data with a verified badge", () => {
    renderWithIntl(<AccountProfile profile={profile} onReload={vi.fn()} />);

    expect(screen.getByText("ana@dazma.store")).toBeInTheDocument();
    expect(screen.getByText("3001234567")).toBeInTheDocument();
    expect(screen.getByText(t("Account.account.verifiedBadge"))).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: t("Account.verifyEmail.button") })).not.toBeInTheDocument();
  });

  it("asks to verify an unverified email", () => {
    renderWithIntl(<AccountProfile profile={{ ...profile, emailVerified: false }} onReload={vi.fn()} />);

    expect(screen.getByText(t("Account.account.unverifiedBadge"))).toBeInTheDocument();
    expect(screen.getByText(t("Account.account.emailNotVerified"))).toBeInTheDocument();
    expect(screen.getByRole("button", { name: t("Account.verifyEmail.button") })).toBeInTheDocument();
  });

  it("announces the sections that are coming soon", async () => {
    renderWithIntl(<AccountProfile profile={profile} onReload={vi.fn()} />);

    await userEvent.click(screen.getByRole("tab", { name: t("Account.account.tabs.publications") }));
    expect(screen.getByText(t("Account.account.publicationsSoon"))).toBeInTheDocument();

    await userEvent.click(screen.getByRole("tab", { name: t("Account.account.tabs.security") }));
    expect(screen.getByText(t("Account.account.securitySoon"))).toBeInTheDocument();
  });

  it("lets the user sign out", () => {
    renderWithIntl(<AccountProfile profile={profile} onReload={vi.fn()} />);

    expect(screen.getByRole("button", { name: t("Account.account.signOut") })).toBeInTheDocument();
  });
});
