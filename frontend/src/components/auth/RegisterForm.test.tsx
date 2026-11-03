import { FirebaseError } from "firebase/app";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const mocks = vi.hoisted(() => {
  const replace = vi.fn();
  return {
    replace,
    router: { replace },
    createAccount: vi.fn(),
    signInWithGoogle: vi.fn(),
    isPopupDismissed: vi.fn(),
    registerUser: vi.fn(),
    getPhoneRules: vi.fn(),
    listMunicipalities: vi.fn(),
  };
});

vi.mock("next/navigation", () => ({ useRouter: () => mocks.router }));
vi.mock("@/lib/auth/sign-in", () => ({
  ACCOUNT_PATH: "/cuenta",
  HOME_PATH: "/",
  createAccount: mocks.createAccount,
  signInWithGoogle: mocks.signInWithGoogle,
  isPopupDismissed: mocks.isPopupDismissed,
}));
vi.mock("@/lib/api/users", () => ({ registerUser: mocks.registerUser, getPhoneRules: mocks.getPhoneRules }));
vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities: mocks.listMunicipalities }));

import { RegisterForm } from "./RegisterForm";

type Fields = { email?: string; password?: string; confirmation?: string; name?: string; phone?: string };

async function fill({ email = "ana@dazma.store", password = "secreto123", confirmation = "secreto123", name = "Ana", phone = "3001234567" }: Fields = {}) {
  await userEvent.type(screen.getByLabelText(t("Auth.register.emailLabel")), email);
  await userEvent.type(screen.getByLabelText(t("Auth.register.passwordLabel")), password);
  await userEvent.type(screen.getByLabelText(t("Auth.register.confirmPasswordLabel")), confirmation);
  await userEvent.type(screen.getByLabelText(t("Auth.profile.nameLabel")), name);
  await userEvent.type(screen.getByLabelText(t("Auth.profile.phoneLabel")), phone);
  await screen.findByRole("option", { name: "Rionegro" });
  await userEvent.selectOptions(screen.getByLabelText(t("Auth.profile.municipalityLabel")), "05615");
}

describe("RegisterForm", () => {
  beforeEach(() => {
    mocks.replace.mockReset();
    mocks.createAccount.mockReset();
    mocks.registerUser.mockReset();
    mocks.getPhoneRules.mockResolvedValue({ countryCode: "57", nationalLength: 10, mobilePrefix: "3" });
    mocks.signInWithGoogle.mockReset();
    mocks.isPopupDismissed.mockReturnValue(false);
    mocks.listMunicipalities.mockResolvedValue([{ code: "05615", name: "Rionegro", department: "Antioquia" }]);
  });

  it("rejects an invalid email first", async () => {
    renderWithIntl(<RegisterForm />);
    await fill({ email: "nada" });

    await userEvent.click(screen.getByRole("button", { name: t("Auth.register.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.invalidEmail"));
    expect(mocks.createAccount).not.toHaveBeenCalled();
  });

  it("rejects a password confirmation that does not match", async () => {
    renderWithIntl(<RegisterForm />);
    await fill({ confirmation: "otra-clave-123" });

    await userEvent.click(screen.getByRole("button", { name: t("Auth.register.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.passwordMismatch"));
  });

  it("rejects an invalid phone", async () => {
    renderWithIntl(<RegisterForm />);
    await fill({ phone: "123" });

    await userEvent.click(screen.getByRole("button", { name: t("Auth.register.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.phoneInvalid"));
  });

  it("creates the account, registers the profile and goes home", async () => {
    mocks.createAccount.mockResolvedValue(undefined);
    mocks.registerUser.mockResolvedValue(undefined);
    renderWithIntl(<RegisterForm />);
    await fill();

    await userEvent.click(screen.getByRole("button", { name: t("Auth.register.submit") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/"));
    expect(mocks.createAccount).toHaveBeenCalledWith("ana@dazma.store", "secreto123");
    expect(mocks.registerUser).toHaveBeenCalledWith({ name: "Ana", phone: "3001234567", municipality: "05615" });
  });

  it("shows the account creation failure and does not register the profile", async () => {
    mocks.createAccount.mockRejectedValue(new FirebaseError("auth/email-already-in-use", "in use"));
    renderWithIntl(<RegisterForm />);
    await fill();

    await userEvent.click(screen.getByRole("button", { name: t("Auth.register.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.emailInUse"));
    expect(mocks.registerUser).not.toHaveBeenCalled();
    expect(screen.getByRole("button", { name: t("Auth.register.submit") })).toBeEnabled();
  });

  it("completes the profile later when registering it fails", async () => {
    mocks.createAccount.mockResolvedValue(undefined);
    mocks.registerUser.mockRejectedValue(new Error("offline"));
    renderWithIntl(<RegisterForm />);
    await fill();

    await userEvent.click(screen.getByRole("button", { name: t("Auth.register.submit") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/cuenta"));
  });

  it("follows the destination chosen after signing in with Google", async () => {
    mocks.signInWithGoogle.mockResolvedValue("/cuenta");
    renderWithIntl(<RegisterForm />);

    await userEvent.click(screen.getByRole("button", { name: t("Auth.register.googleButton") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/cuenta"));
  });
});
