import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const { replace, signOutCompletely } = vi.hoisted(() => ({ replace: vi.fn(), signOutCompletely: vi.fn() }));

vi.mock("next/navigation", () => ({ useRouter: () => ({ replace }) }));
vi.mock("@/lib/auth/sign-in", () => ({ LOGIN_PATH: "/ingresar", signOutCompletely }));

import { SignOutButton } from "./SignOutButton";

describe("SignOutButton", () => {
  it("signs out and goes to the login page", async () => {
    signOutCompletely.mockResolvedValue(undefined);
    renderWithIntl(<SignOutButton />);

    await userEvent.click(screen.getByRole("button", { name: t("Account.account.signOut") }));

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/ingresar"));
    expect(signOutCompletely).toHaveBeenCalledTimes(1);
  });
});
