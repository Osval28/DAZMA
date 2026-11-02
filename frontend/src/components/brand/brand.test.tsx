import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { IconBackdrop } from "./IconBackdrop";
import { LogoMark } from "./LogoMark";
import { Wordmark } from "./Wordmark";

describe("brand", () => {
  it("draws the logo as a decorative svg", () => {
    const { container } = render(<LogoMark />);

    expect(container.querySelector("svg")).toHaveAttribute("aria-hidden", "true");
  });

  it("links the wordmark to the home page", () => {
    render(<Wordmark />);

    expect(screen.getByRole("link", { name: "DAZMA" })).toHaveAttribute("href", "/");
  });

  it("renders the backdrop in its default and subtle tones", () => {
    const standard = render(<IconBackdrop />);
    expect(standard.container.firstChild).not.toBeNull();
    standard.unmount();

    const subtle = render(<IconBackdrop tone="subtle" />);
    expect(subtle.container.firstChild).not.toBeNull();
  });
});
