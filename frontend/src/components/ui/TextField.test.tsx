import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { TextField } from "./TextField";

describe("TextField", () => {
  it("links the label to the input", () => {
    render(<TextField id="email" label="Correo" />);

    expect(screen.getByLabelText("Correo")).toHaveAttribute("id", "email");
  });

  it("passes the remaining attributes to the input", () => {
    render(<TextField id="email" label="Correo" type="email" name="email" placeholder="tu@correo.co" />);

    const input = screen.getByLabelText("Correo");
    expect(input).toHaveAttribute("type", "email");
    expect(input).toHaveAttribute("name", "email");
    expect(input).toHaveAttribute("placeholder", "tu@correo.co");
  });

  it("accepts typing", async () => {
    render(<TextField id="name" label="Nombre" />);

    await userEvent.type(screen.getByLabelText("Nombre"), "Ana");

    expect(screen.getByLabelText("Nombre")).toHaveValue("Ana");
  });
});
