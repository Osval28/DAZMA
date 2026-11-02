import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const mocks = vi.hoisted(() => {
  const replace = vi.fn();
  return { replace, router: { replace }, useSessionProfile: vi.fn() };
});

vi.mock("next/navigation", () => ({ useRouter: () => mocks.router }));
vi.mock("@/components/account/useSessionProfile", () => ({ useSessionProfile: mocks.useSessionProfile }));
vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header /> }));
vi.mock("@/components/account/VerifyEmailButton", () => ({
  VerifyEmailButton: ({ returnPath }: { returnPath: string }) => <button type="button">verificar {returnPath}</button>,
}));
vi.mock("./PublicationForm", () => ({ PublicationForm: () => <p>formulario de publicación</p> }));

import { NewPublicationView } from "./NewPublicationView";

describe("NewPublicationView", () => {
  beforeEach(() => {
    mocks.replace.mockReset();
    mocks.useSessionProfile.mockReset();
  });

  it("asks to come back to the creation page after signing in", () => {
    mocks.useSessionProfile.mockReturnValue({ state: { status: "loading" }, reload: vi.fn() });

    renderWithIntl(<NewPublicationView />);

    expect(mocks.useSessionProfile).toHaveBeenCalledWith("/publicaciones/nueva");
    expect(screen.getByRole("heading", { name: t("Publications.create.title") })).toBeInTheDocument();
    expect(screen.getByText(t("Account.loading"))).toBeInTheDocument();
    expect(screen.queryByText("formulario de publicación")).not.toBeInTheDocument();
  });

  it("sends users without profile to complete it", () => {
    mocks.useSessionProfile.mockReturnValue({ state: { status: "missing" }, reload: vi.fn() });

    renderWithIntl(<NewPublicationView />);

    expect(mocks.replace).toHaveBeenCalledWith("/cuenta");
  });

  it("shows the failure", () => {
    mocks.useSessionProfile.mockReturnValue({ state: { status: "failed", errorKey: "Auth.errors.network" }, reload: vi.fn() });

    renderWithIntl(<NewPublicationView />);

    expect(screen.getByRole("alert")).toHaveTextContent(t("Auth.errors.network"));
  });

  it("asks to verify the email before publishing", () => {
    mocks.useSessionProfile.mockReturnValue({ state: { status: "ready", profile: { emailVerified: false } }, reload: vi.fn() });

    renderWithIntl(<NewPublicationView />);

    expect(screen.getByRole("alert")).toHaveTextContent(t("Publications.create.emailNotVerified"));
    expect(screen.getByRole("button", { name: "verificar /publicaciones/nueva" })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Publications.create.goToAccount") })).toHaveAttribute("href", "/cuenta");
    expect(screen.queryByText("formulario de publicación")).not.toBeInTheDocument();
  });

  it("shows the form to a verified user", () => {
    mocks.useSessionProfile.mockReturnValue({ state: { status: "ready", profile: { emailVerified: true } }, reload: vi.fn() });

    renderWithIntl(<NewPublicationView />);

    expect(screen.getByText("formulario de publicación")).toBeInTheDocument();
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
  });
});
