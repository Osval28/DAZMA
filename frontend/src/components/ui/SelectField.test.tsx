import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { SelectField } from "./SelectField";

const options = [
  { value: "05615", label: "Rionegro" },
  { value: "05001", label: "Medellín" },
];

describe("SelectField", () => {
  it("starts on a disabled placeholder", () => {
    render(<SelectField id="municipality" label="Municipio" placeholder="Elige" options={options} />);

    expect(screen.getByLabelText("Municipio")).toHaveValue("");
    expect(screen.getByRole("option", { name: "Elige" })).toBeDisabled();
  });

  it("lists every option", () => {
    render(<SelectField id="municipality" label="Municipio" placeholder="Elige" options={options} />);

    expect(screen.getByRole("option", { name: "Rionegro" })).toHaveValue("05615");
    expect(screen.getByRole("option", { name: "Medellín" })).toHaveValue("05001");
  });

  it("lets the user choose an option", async () => {
    render(<SelectField id="municipality" label="Municipio" placeholder="Elige" options={options} />);

    await userEvent.selectOptions(screen.getByLabelText("Municipio"), "05615");

    expect(screen.getByLabelText("Municipio")).toHaveValue("05615");
  });

  it("can be disabled", () => {
    render(<SelectField id="municipality" label="Municipio" placeholder="Elige" options={[]} disabled />);

    expect(screen.getByLabelText("Municipio")).toBeDisabled();
  });
});
