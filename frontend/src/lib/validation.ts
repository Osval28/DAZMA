import type { PhoneRules } from "@/lib/api/users";

export type ValidationIssue =
  | "EMAIL_INVALID"
  | "PASSWORD_REQUIRED"
  | "PASSWORD_TOO_SHORT"
  | "PASSWORD_MISMATCH"
  | "NAME_REQUIRED"
  | "NAME_TOO_LONG"
  | "PHONE_INVALID"
  | "MUNICIPALITY_REQUIRED";

export type ProfileInput = {
  name: string;
  phone: string;
  municipality: string;
};

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_SEPARATORS = /[\s\-().+]/g;
const DIGITS_ONLY = /^[0-9]+$/;
const MINIMUM_PASSWORD_LENGTH = 8;
const MAXIMUM_NAME_LENGTH = 255;

export function normalizePhone(phone: string): string {
  return phone.replace(PHONE_SEPARATORS, "");
}

export function validateEmail(email: string): ValidationIssue | undefined {
  return EMAIL_PATTERN.test(email) ? undefined : "EMAIL_INVALID";
}

export function validateCurrentPassword(password: string): ValidationIssue | undefined {
  return password.length > 0 ? undefined : "PASSWORD_REQUIRED";
}

export function validateNewPassword(password: string, confirmation: string): ValidationIssue | undefined {
  if (password.length < MINIMUM_PASSWORD_LENGTH) {
    return "PASSWORD_TOO_SHORT";
  }
  return password === confirmation ? undefined : "PASSWORD_MISMATCH";
}

export function validateProfile({ name, phone, municipality }: ProfileInput, rules: PhoneRules): ValidationIssue | undefined {
  if (name.length === 0) {
    return "NAME_REQUIRED";
  }
  if (name.length > MAXIMUM_NAME_LENGTH) {
    return "NAME_TOO_LONG";
  }
  const national = phone.length === rules.countryCode.length + rules.nationalLength && phone.startsWith(rules.countryCode)
    ? phone.slice(rules.countryCode.length)
    : phone;
  if (national.length !== rules.nationalLength || !national.startsWith(rules.mobilePrefix) || !DIGITS_ONLY.test(national)) {
    return "PHONE_INVALID";
  }
  return municipality.length === 0 ? "MUNICIPALITY_REQUIRED" : undefined;
}
