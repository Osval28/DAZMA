import { describe, expect, it } from "vitest";
import { toneOf } from "@/lib/account/avatar-tone";
import { renderWithIntl, t } from "@/test/intl";
import { Avatar } from "./Avatar";

describe("Avatar", () => {
  it("shows the initial of the name and is decorative", () => {
    const { container } = renderWithIntl(<Avatar name="ana" />);

    const avatar = container.firstElementChild as HTMLElement;
    expect(avatar).toHaveTextContent("A");
    expect(avatar).toHaveAttribute("aria-hidden", "true");
    expect(avatar).toHaveClass("avatar", "avatar--regular");
  });

  it("shows a placeholder when the name is unknown", () => {
    const { container } = renderWithIntl(<Avatar name={undefined} />);

    expect(container.firstElementChild).toHaveTextContent(t("Account.header.unknownInitial"));
  });

  it("applies the requested size", () => {
    const { container } = renderWithIntl(<Avatar name="Ana" size="large" />);

    expect(container.firstElementChild).toHaveClass("avatar--large");
  });

  it("applies a tone derived from the name only when tinted", () => {
    const tinted = renderWithIntl(<Avatar name="Ana" tinted />);
    expect(tinted.container.firstElementChild).toHaveClass(`avatar--tone-${toneOf("Ana")}`);
    tinted.unmount();

    const plain = renderWithIntl(<Avatar name="Ana" />);
    expect(plain.container.firstElementChild?.className).not.toContain("tone");
  });

  it("does not tint an unknown name", () => {
    const { container } = renderWithIntl(<Avatar name={undefined} tinted />);

    expect(container.firstElementChild?.className).not.toContain("tone");
  });
});
