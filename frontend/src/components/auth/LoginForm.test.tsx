import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "@/lib/api/errors";
import { renderWithIntl, t } from "@/test/intl";

const mocks = vi.hoisted(() => {
  const replace = vi.fn();
  return {
    replace,
    router: { replace },
    searchParams: new URLSearchParams(),
    signInWithEmail: vi.fn(),
    signInWithGoogle: vi.fn(),
    isPopupDismissed: vi.fn(),
  };
});

vi.mock("next/navigation", () => ({ useRouter: () => mocks.router, useSearchParams: () => mocks.searchParams }));
vi.mock("@/lib/auth/sign-in", () => ({
  HOME_PATH: "/",
  signInWithEmail: mocks.signInWithEmail,
  signInWithGoogle: mocks.signInWithGoogle,
  isPopupDismissed: mocks.isPopupDismissed,
}));

import { LoginForm } from "./LoginForm";

async function fill(email: string, password: string) {
  if (email !== "") {
    await userEvent.type(screen.getByLabelText(t("Auth.login.emailLabel")), email);
  }
  if (password !== "") {
    await userEvent.type(screen.getByLabelText(t("Auth.login.passwordLabel")), password);
  }
}

describe("LoginForm", () => {
  beforeEach(() => {
    mocks.replace.mockReset();
    mocks.signInWithEmail.mockReset();
    mocks.signInWithGoogle.mockReset();
    mocks.isPopupDismissed.mockReturnValue(false);
    mocks.searchParams.delete("next");
  });

  it("rejects an invalid email without calling Firebase", async () => {
    renderWithIntl(<LoginForm />);
    await fill("no-es-correo", "secreto123");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.invalidEmail"));
    expect(mocks.signInWithEmail).not.toHaveBeenCalled();
  });

  it("requires a password", async () => {
    renderWithIntl(<LoginForm />);
    await fill("ana@dazma.store", "");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.passwordRequired"));
  });

  it("returns to the page the user came from", async () => {
    mocks.searchParams.set("next", "/chats");
    mocks.signInWithEmail.mockResolvedValue("/");
    renderWithIntl(<LoginForm />);
    await fill("ana@dazma.store", "secreto123");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.submit") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/chats"));
    expect(mocks.signInWithEmail).toHaveBeenCalledWith("ana@dazma.store", "secreto123");
  });

  it("ignores an unsafe return address", async () => {
    mocks.searchParams.set("next", "https://evil.example");
    mocks.signInWithEmail.mockResolvedValue("/");
    renderWithIntl(<LoginForm />);
    await fill("ana@dazma.store", "secreto123");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.submit") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/"));
  });

  it("completes the profile first when the user has none", async () => {
    mocks.searchParams.set("next", "/chats");
    mocks.signInWithEmail.mockResolvedValue("/cuenta");
    renderWithIntl(<LoginForm />);
    await fill("ana@dazma.store", "secreto123");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.submit") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/cuenta"));
  });

  it("shows the failure and lets the user retry", async () => {
    mocks.signInWithEmail.mockRejectedValue(new ApiError(401, "AUTHENTICATION_REQUIRED"));
    renderWithIntl(<LoginForm />);
    await fill("ana@dazma.store", "secreto123");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.sessionExpired"));
    expect(screen.getByRole("button", { name: t("Auth.login.submit") })).toBeEnabled();
  });

  it("links to the password recovery page", () => {
    renderWithIntl(<LoginForm />);

    expect(screen.getByRole("link", { name: t("Auth.login.forgotPassword") })).toHaveAttribute("href", "/recuperar-contrasena");
  });

  it("signs in with Google and follows the same destination rules", async () => {
    mocks.searchParams.set("next", "/chats");
    mocks.signInWithGoogle.mockResolvedValue("/");
    renderWithIntl(<LoginForm />);

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.googleButton") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/chats"));
  });

  it("shows the Google failure", async () => {
    mocks.signInWithGoogle.mockRejectedValue(new Error("boom"));
    renderWithIntl(<LoginForm />);

    await userEvent.click(screen.getByRole("button", { name: t("Auth.login.googleButton") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.generic"));
  });
});
