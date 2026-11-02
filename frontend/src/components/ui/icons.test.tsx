import { render } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { CloudOffIcon, EyeIcon, EyeOffIcon, GoogleIcon, PinIcon, SearchIcon } from "./icons";

describe("icons", () => {
  it.each([
    ["EyeIcon", EyeIcon],
    ["EyeOffIcon", EyeOffIcon],
    ["GoogleIcon", GoogleIcon],
    ["SearchIcon", SearchIcon],
    ["PinIcon", PinIcon],
    ["CloudOffIcon", CloudOffIcon],
  ])("%s is a decorative svg", (_name, Icon) => {
    const { container } = render(<Icon />);

    const svg = container.querySelector("svg");
    expect(svg).not.toBeNull();
    expect(svg).toHaveAttribute("aria-hidden", "true");
  });
});
