import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "@/lib/api/errors";
import { renderWithIntl, t } from "@/test/intl";

const { getMyProfile } = vi.hoisted(() => ({ getMyProfile: vi.fn() }));

vi.mock("@/lib/api/users", () => ({ getMyProfile }));
vi.mock("@/i18n/actions", () => ({ setLocale: vi.fn() }));

import { SiteHeader } from "./SiteHeader";

describe("SiteHeader", () => {
  beforeEach(() => {
    getMyProfile.mockReset();
  });

  it("offers the member shortcuts", async () => {
    getMyProfile.mockResolvedValue({ name: "Ana" });

    renderWithIntl(<SiteHeader />);

    expect(await screen.findByRole("link", { name: t("Chats.list.headerLink") })).toHaveAttribute("href", "/chats");
    expect(screen.getByRole("link", { name: t("Publications.create.headerAction") })).toHaveAttribute("href", "/publicaciones/nueva");
    expect(screen.getByRole("link", { name: t("Account.header.accountOf", { name: "Ana" }) })).toHaveAttribute("href", "/cuenta");
    expect(screen.queryByRole("link", { name: t("Account.header.signIn") })).not.toBeInTheDocument();
  });

  it("offers signing in and registering to anonymous visitors", async () => {
    getMyProfile.mockRejectedValue(new ApiError(401, "AUTHENTICATION_REQUIRED"));

    renderWithIntl(<SiteHeader />);

    expect(await screen.findByRole("link", { name: t("Account.header.signIn") })).toHaveAttribute("href", "/ingresar");
    expect(screen.getByRole("link", { name: t("Account.header.signUp") })).toHaveAttribute("href", "/registro");
    expect(screen.queryByRole("link", { name: t("Chats.list.headerLink") })).not.toBeInTheDocument();
  });

  it("shows neither group while the session is loading", () => {
    getMyProfile.mockReturnValue(new Promise(() => undefined));

    renderWithIntl(<SiteHeader />);

    expect(screen.queryByRole("link", { name: t("Account.header.signIn") })).not.toBeInTheDocument();
    expect(screen.queryByRole("link", { name: t("Chats.list.headerLink") })).not.toBeInTheDocument();
    expect(screen.getByRole("link", { name: "DAZMA" })).toBeInTheDocument();
  });
});
