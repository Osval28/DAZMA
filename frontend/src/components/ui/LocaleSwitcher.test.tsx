import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t, translatorFor } from "@/test/intl";

const { setLocale } = vi.hoisted(() => ({ setLocale: vi.fn() }));

vi.mock("@/i18n/actions", () => ({ setLocale }));

import { LocaleSwitcher } from "./LocaleSwitcher";

describe("LocaleSwitcher", () => {
  it("offers the other language from Spanish", () => {
    renderWithIntl(<LocaleSwitcher />);

    const button = screen.getByRole("button", { name: t("Locale.switchTo", { language: t("Locale.languages.en") }) });
    expect(button).toHaveTextContent("EN");
    expect(button).toHaveAttribute("lang", "en");
  });

  it("offers the other language from English", () => {
    const english = translatorFor("en");

    renderWithIntl(<LocaleSwitcher />, "en");

    const button = screen.getByRole("button", { name: english("Locale.switchTo", { language: english("Locale.languages.es") }) });
    expect(button).toHaveTextContent("ES");
  });

  it("saves the chosen locale", async () => {
    setLocale.mockResolvedValue(undefined);
    renderWithIntl(<LocaleSwitcher />);

    await userEvent.click(screen.getByRole("button"));

    await waitFor(() => expect(setLocale).toHaveBeenCalledWith("en"));
  });
});
