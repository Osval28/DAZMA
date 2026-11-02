import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { Alert } from "./Alert";

describe("Alert", () => {
  it("announces its content as an alert", () => {
    render(<Alert>Algo salió mal</Alert>);

    expect(screen.getByRole("alert")).toHaveTextContent("Algo salió mal");
  });
});
