import { normalizePhone, type ProfileInput } from "@/lib/validation";

export function readProfileInput(form: FormData): ProfileInput {
  return {
    name: String(form.get("name")).trim(),
    phone: normalizePhone(String(form.get("phone"))),
    municipality: String(form.get("municipality")),
  };
}
