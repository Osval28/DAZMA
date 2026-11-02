import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl } from "@/test/intl";

const { signInWithGoogle, isPopupDismissed } = vi.hoisted(() => ({ signInWithGoogle: vi.fn(), isPopupDismissed: vi.fn() }));

vi.mock("@/lib/auth/sign-in", () => ({ signInWithGoogle, isPopupDismissed }));

import { GoogleButton } from "./GoogleButton";

function renderButton(overrides: Partial<React.ComponentProps<typeof GoogleButton>> = {}) {
  const onSignedIn = vi.fn();
  const onFailure = vi.fn();
  renderWithIntl(<GoogleButton label="Continuar con Google" dividerLabel="o" disabled={false} onSignedIn={onSignedIn} onFailure={onFailure} {...overrides} />);
  return { onSignedIn, onFailure };
}

describe("GoogleButton", () => {
  beforeEach(() => {
    signInWithGoogle.mockReset();
    isPopupDismissed.mockReset();
    isPopupDismissed.mockReturnValue(false);
  });

  it("shows the divider and the button", () => {
    renderButton();

    expect(screen.getByRole("separator")).toHaveTextContent("o");
    expect(screen.getByRole("button", { name: "Continuar con Google" })).toBeEnabled();
  });

  it("reports where to go after signing in", async () => {
    signInWithGoogle.mockResolvedValue("/cuenta");
    const { onSignedIn } = renderButton();

    await userEvent.click(screen.getByRole("button"));

    await waitFor(() => expect(onSignedIn).toHaveBeenCalledWith("/cuenta"));
  });

  it("reports a failure with its message key", async () => {
    signInWithGoogle.mockRejectedValue(new Error("boom"));
    const { onFailure } = renderButton();

    await userEvent.click(screen.getByRole("button"));

    await waitFor(() => expect(onFailure).toHaveBeenCalledWith("Auth.errors.generic"));
    expect(screen.getByRole("button")).toBeEnabled();
  });

  it("stays silent when the user closes the popup", async () => {
    signInWithGoogle.mockRejectedValue(new Error("closed"));
    isPopupDismissed.mockReturnValue(true);
    const { onFailure } = renderButton();

    await userEvent.click(screen.getByRole("button"));

    await waitFor(() => expect(signInWithGoogle).toHaveBeenCalled());
    expect(onFailure).not.toHaveBeenCalled();
    expect(screen.getByRole("button")).toBeEnabled();
  });

  it("is disabled when the form is busy", () => {
    renderButton({ disabled: true });

    expect(screen.getByRole("button")).toBeDisabled();
  });

  it("is disabled while the popup is open", async () => {
    signInWithGoogle.mockReturnValue(new Promise(() => undefined));
    renderButton();

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByRole("button")).toBeDisabled();
  });
});
