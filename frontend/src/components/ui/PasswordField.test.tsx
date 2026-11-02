import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { PasswordField } from "./PasswordField";

describe("PasswordField", () => {
  it("hides the password by default", () => {
    renderWithIntl(<PasswordField id="password" label="Contraseña" />);

    expect(screen.getByLabelText("Contraseña")).toHaveAttribute("type", "password");
  });

  it("reveals and hides the password with the toggle", async () => {
    renderWithIntl(<PasswordField id="password" label="Contraseña" />);

    await userEvent.click(screen.getByRole("button", { name: t("Auth.passwordToggle.show") }));
    expect(screen.getByLabelText("Contraseña")).toHaveAttribute("type", "text");

    await userEvent.click(screen.getByRole("button", { name: t("Auth.passwordToggle.hide") }));
    expect(screen.getByLabelText("Contraseña")).toHaveAttribute("type", "password");
  });

  it("keeps what the user typed when toggling", async () => {
    renderWithIntl(<PasswordField id="password" label="Contraseña" />);

    await userEvent.type(screen.getByLabelText("Contraseña"), "secreto123");
    await userEvent.click(screen.getByRole("button", { name: t("Auth.passwordToggle.show") }));

    expect(screen.getByLabelText("Contraseña")).toHaveValue("secreto123");
  });
});
