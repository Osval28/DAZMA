import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const { useSessionProfile } = vi.hoisted(() => ({ useSessionProfile: vi.fn() }));

vi.mock("./useSessionProfile", () => ({ useSessionProfile }));
vi.mock("./SiteHeader", () => ({ SiteHeader: () => <header data-testid="site-header" /> }));
vi.mock("./AccountProfile", () => ({ AccountProfile: ({ profile }: { profile: { name: string } }) => <p>perfil de {profile.name}</p> }));
vi.mock("./ProfileForm", () => ({ ProfileForm: () => <p>formulario de perfil</p> }));
vi.mock("./SignOutButton", () => ({ SignOutButton: () => <button type="button">salir</button> }));
vi.mock("@/components/auth/AuthShell", () => ({
  AuthShell: ({ title, children, footer }: { title: string; children: React.ReactNode; footer: React.ReactNode }) => (
    <section>
      <h1>{title}</h1>
      {children}
      {footer}
    </section>
  ),
}));

import { AccountView } from "./AccountView";

describe("AccountView", () => {
  beforeEach(() => {
    useSessionProfile.mockReset();
  });

  it("shows a loading message", () => {
    useSessionProfile.mockReturnValue({ state: { status: "loading" }, reload: vi.fn() });

    renderWithIntl(<AccountView />);

    expect(screen.getByText(t("Account.loading"))).toBeInTheDocument();
  });

  it("shows the failure", () => {
    useSessionProfile.mockReturnValue({ state: { status: "failed", errorKey: "Auth.errors.network" }, reload: vi.fn() });

    renderWithIntl(<AccountView />);

    expect(screen.getByRole("alert")).toHaveTextContent(t("Auth.errors.network"));
  });

  it("asks to complete the profile when it does not exist yet", () => {
    useSessionProfile.mockReturnValue({ state: { status: "missing" }, reload: vi.fn() });

    renderWithIntl(<AccountView />);

    expect(screen.getByRole("heading", { name: t("Account.completeProfile.title") })).toBeInTheDocument();
    expect(screen.getByText("formulario de perfil")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "salir" })).toBeInTheDocument();
  });

  it("shows the account of a registered user", () => {
    useSessionProfile.mockReturnValue({ state: { status: "ready", profile: { name: "Ana" } }, reload: vi.fn() });

    renderWithIntl(<AccountView />);

    expect(screen.getByText("perfil de Ana")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Account.account.backHome") })).toHaveAttribute("href", "/");
  });
});
