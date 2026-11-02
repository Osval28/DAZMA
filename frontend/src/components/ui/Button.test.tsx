import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { Button } from "./Button";

describe("Button", () => {
  it("is a primary non-submitting button by default", () => {
    render(<Button>Guardar</Button>);

    const button = screen.getByRole("button", { name: "Guardar" });
    expect(button).toHaveAttribute("type", "button");
    expect(button).toHaveClass("button", "button--primary");
  });

  it("applies the requested variant and extra classes", () => {
    render(
      <Button variant="secondary" className="extra">
        Cancelar
      </Button>,
    );

    expect(screen.getByRole("button")).toHaveClass("button", "button--secondary", "extra");
  });

  it("can submit forms", () => {
    render(<Button type="submit">Enviar</Button>);

    expect(screen.getByRole("button")).toHaveAttribute("type", "submit");
  });

  it("calls the click handler", async () => {
    const onClick = vi.fn();
    render(<Button onClick={onClick}>Pulsar</Button>);

    await userEvent.click(screen.getByRole("button"));

    expect(onClick).toHaveBeenCalledTimes(1);
  });

  it("does not click while disabled", async () => {
    const onClick = vi.fn();
    render(
      <Button onClick={onClick} disabled>
        Pulsar
      </Button>,
    );

    await userEvent.click(screen.getByRole("button"));

    expect(onClick).not.toHaveBeenCalled();
  });
});
