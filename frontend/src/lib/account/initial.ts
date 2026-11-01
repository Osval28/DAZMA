export function initialOf(name: string): string {
  const [first] = Array.from(name.trim());
  return first === undefined ? "" : first.toLocaleUpperCase("es-CO");
}
