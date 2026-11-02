import { FirebaseError } from "firebase/app";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const { requestPasswordReset } = vi.hoisted(() => ({ requestPasswordReset: vi.fn() }));

vi.mock("@/lib/auth/password-reset", () => ({ requestPasswordReset }));

import { PasswordResetForm } from "./PasswordResetForm";

describe("PasswordResetForm", () => {
  beforeEach(() => {
    requestPasswordReset.mockReset();
  });

  it("rejects an invalid email", async () => {
    renderWithIntl(<PasswordResetForm />);
    await userEvent.type(screen.getByLabelText(t("Auth.passwordReset.emailLabel")), "nada");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.passwordReset.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.invalidEmail"));
    expect(requestPasswordReset).not.toHaveBeenCalled();
  });

  it("confirms the request with a neutral message", async () => {
    requestPasswordReset.mockResolvedValue(undefined);
    renderWithIntl(<PasswordResetForm />);
    await userEvent.type(screen.getByLabelText(t("Auth.passwordReset.emailLabel")), "  ana@dazma.store ");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.passwordReset.submit") }));

    expect(await screen.findByRole("status")).toHaveTextContent(t("Auth.passwordReset.sent"));
    expect(requestPasswordReset).toHaveBeenCalledWith("ana@dazma.store");
    expect(screen.queryByLabelText(t("Auth.passwordReset.emailLabel"))).not.toBeInTheDocument();
  });

  it("shows a failure and keeps the form", async () => {
    requestPasswordReset.mockRejectedValue(new FirebaseError("auth/too-many-requests", "slow down"));
    renderWithIntl(<PasswordResetForm />);
    await userEvent.type(screen.getByLabelText(t("Auth.passwordReset.emailLabel")), "ana@dazma.store");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.passwordReset.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.tooManyRequests"));
    expect(screen.getByRole("button", { name: t("Auth.passwordReset.submit") })).toBeEnabled();
  });

  it("is disabled while sending", async () => {
    requestPasswordReset.mockReturnValue(new Promise(() => undefined));
    renderWithIntl(<PasswordResetForm />);
    await userEvent.type(screen.getByLabelText(t("Auth.passwordReset.emailLabel")), "ana@dazma.store");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.passwordReset.submit") }));

    expect(screen.getByRole("button", { name: t("Auth.passwordReset.submitting") })).toBeDisabled();
  });
});
