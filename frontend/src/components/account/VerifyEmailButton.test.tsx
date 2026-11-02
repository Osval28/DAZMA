import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { NetworkError } from "@/lib/api/errors";
import { renderWithIntl, t } from "@/test/intl";

const { replace, refreshVerifiedSession } = vi.hoisted(() => ({ replace: vi.fn(), refreshVerifiedSession: vi.fn() }));

vi.mock("next/navigation", () => ({ useRouter: () => ({ replace }) }));
vi.mock("@/lib/auth/email-verification", () => ({ refreshVerifiedSession }));

import { VerifyEmailButton } from "./VerifyEmailButton";

describe("VerifyEmailButton", () => {
  const onRefreshed = vi.fn();

  beforeEach(() => {
    replace.mockReset();
    refreshVerifiedSession.mockReset();
    onRefreshed.mockReset();
    onRefreshed.mockResolvedValue(undefined);
  });

  it("reloads the profile after refreshing the session", async () => {
    refreshVerifiedSession.mockResolvedValue(true);
    renderWithIntl(<VerifyEmailButton returnPath="/cuenta" onRefreshed={onRefreshed} />);

    await userEvent.click(screen.getByRole("button", { name: t("Account.verifyEmail.button") }));

    await waitFor(() => expect(onRefreshed).toHaveBeenCalledTimes(1));
  });

  it("hints that the email is still unverified after reloading", async () => {
    refreshVerifiedSession.mockResolvedValue(true);
    renderWithIntl(<VerifyEmailButton returnPath="/cuenta" onRefreshed={onRefreshed} />);

    await userEvent.click(screen.getByRole("button"));

    expect(await screen.findByRole("status")).toHaveTextContent(t("Account.verifyEmail.stillUnverified"));
  });

  it("sends the user to sign in when there is no Firebase session", async () => {
    refreshVerifiedSession.mockResolvedValue(false);
    renderWithIntl(<VerifyEmailButton returnPath="/publicaciones/nueva" onRefreshed={onRefreshed} />);

    await userEvent.click(screen.getByRole("button"));

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/ingresar?next=%2Fpublicaciones%2Fnueva"));
    expect(onRefreshed).not.toHaveBeenCalled();
  });

  it("shows the failure", async () => {
    refreshVerifiedSession.mockRejectedValue(new NetworkError());
    renderWithIntl(<VerifyEmailButton returnPath="/cuenta" onRefreshed={onRefreshed} />);

    await userEvent.click(screen.getByRole("button"));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.network"));
    expect(screen.getByRole("button")).toBeEnabled();
  });

  it("is disabled while checking", async () => {
    let finish: (value: boolean) => void = () => undefined;
    refreshVerifiedSession.mockReturnValue(new Promise<boolean>((done) => (finish = done)));
    renderWithIntl(<VerifyEmailButton returnPath="/cuenta" onRefreshed={onRefreshed} />);

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByRole("button", { name: t("Account.verifyEmail.checking") })).toBeDisabled();
    finish(false);
    await waitFor(() => expect(replace).toHaveBeenCalled());
  });
});
