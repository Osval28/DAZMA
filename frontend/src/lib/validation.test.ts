import { describe, expect, it } from "vitest";
import {
  normalizePhone,
  validateCurrentPassword,
  validateEmail,
  validateNewPassword,
  validateProfile,
} from "./validation";

describe("normalizePhone", () => {
  it("removes spaces, dashes, parentheses and plus signs", () => {
    expect(normalizePhone("+57 (300) 123-4567")).toBe("573001234567");
  });
});

describe("validateEmail", () => {
  it.each(["ana@dazma.store", "a.b+c@sub.dominio.co"])("accepts %s", (email) => {
    expect(validateEmail(email)).toBeUndefined();
  });

  it.each(["", "ana", "ana@", "@dazma.store", "ana@dazma", "a na@dazma.store"])("rejects %j", (email) => {
    expect(validateEmail(email)).toBe("EMAIL_INVALID");
  });
});

describe("validateCurrentPassword", () => {
  it("requires a password", () => {
    expect(validateCurrentPassword("")).toBe("PASSWORD_REQUIRED");
    expect(validateCurrentPassword("x")).toBeUndefined();
  });
});

describe("validateNewPassword", () => {
  it("requires at least eight characters", () => {
    expect(validateNewPassword("1234567", "1234567")).toBe("PASSWORD_TOO_SHORT");
    expect(validateNewPassword("12345678", "12345678")).toBeUndefined();
  });

  it("requires the confirmation to match", () => {
    expect(validateNewPassword("12345678", "12345679")).toBe("PASSWORD_MISMATCH");
  });
});

describe("validateProfile", () => {
  const valid = { name: "Ana", phone: "3001234567", municipality: "05615" };

  it("accepts a complete profile", () => {
    expect(validateProfile(valid)).toBeUndefined();
  });

  it("requires a name", () => {
    expect(validateProfile({ ...valid, name: "" })).toBe("NAME_REQUIRED");
  });

  it("limits the name length", () => {
    expect(validateProfile({ ...valid, name: "x".repeat(255) })).toBeUndefined();
    expect(validateProfile({ ...valid, name: "x".repeat(256) })).toBe("NAME_TOO_LONG");
  });

  it.each(["", "2001234567", "300123456", "30012345678", "abcdefghij"])("rejects the phone %j", (phone) => {
    expect(validateProfile({ ...valid, phone })).toBe("PHONE_INVALID");
  });

  it("requires a municipality", () => {
    expect(validateProfile({ ...valid, municipality: "" })).toBe("MUNICIPALITY_REQUIRED");
  });
});
