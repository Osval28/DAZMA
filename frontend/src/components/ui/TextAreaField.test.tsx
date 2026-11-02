import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { TextAreaField } from "./TextAreaField";

describe("TextAreaField", () => {
  it("links the label to a multiline input", () => {
    render(<TextAreaField id="description" label="Descripción" rows={5} maxLength={10} />);

    const field = screen.getByLabelText("Descripción");
    expect(field.tagName).toBe("TEXTAREA");
    expect(field).toHaveAttribute("rows", "5");
    expect(field).toHaveAttribute("maxlength", "10");
  });
});
