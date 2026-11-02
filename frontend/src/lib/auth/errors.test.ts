import { FirebaseError } from "firebase/app";
import { describe, expect, it } from "vitest";
import { ApiError, CsrfUnavailableError, NetworkError } from "@/lib/api/errors";
import type { ValidationIssue } from "@/lib/validation";
import { errorKey, validationIssueKey } from "./errors";

describe("errorKey", () => {
  it.each([
    ["auth/invalid-credential", "Auth.errors.invalidCredentials"],
    ["auth/invalid-email", "Auth.errors.invalidEmail"],
    ["auth/user-disabled", "Auth.errors.userDisabled"],
    ["auth/too-many-requests", "Auth.errors.tooManyRequests"],
    ["auth/email-already-in-use", "Auth.errors.emailInUse"],
    ["auth/weak-password", "Auth.errors.weakPassword"],
    ["auth/popup-blocked", "Auth.errors.popupBlocked"],
    ["auth/account-exists-with-different-credential", "Auth.errors.differentProvider"],
    ["auth/network-request-failed", "Auth.errors.network"],
  ])("maps the Firebase code %s", (code, expected) => {
    expect(errorKey(new FirebaseError(code, "message"))).toBe(expected);
  });

  it("uses the generic message for an unknown Firebase code", () => {
    expect(errorKey(new FirebaseError("auth/something-new", "message"))).toBe("Auth.errors.generic");
  });

  it.each([
    ["AUTHENTICATION_REQUIRED", "Auth.errors.sessionExpired"],
    ["IDENTITY_TOKEN_INVALID", "Auth.errors.sessionExpired"],
    ["RECENT_SIGN_IN_REQUIRED", "Auth.errors.recentSignInRequired"],
    ["ORIGIN_NOT_ALLOWED", "Auth.errors.originNotAllowed"],
    ["USER_ALREADY_REGISTERED", "Auth.errors.alreadyRegistered"],
    ["MUNICIPALITY_NOT_FOUND", "Auth.errors.municipalityNotFound"],
    ["USER_PHONE_INVALID", "Auth.errors.phoneInvalid"],
    ["USER_PHONE_REQUIRED", "Auth.errors.phoneInvalid"],
    ["USER_NAME_REQUIRED", "Auth.errors.nameRequired"],
    ["USER_NAME_TOO_LONG", "Auth.errors.nameTooLong"],
  ])("maps the API code %s", (code, expected) => {
    expect(errorKey(new ApiError(400, code))).toBe(expected);
  });

  it("uses the generic message for an unknown API code", () => {
    expect(errorKey(new ApiError(500, "SOMETHING_ELSE"))).toBe("Auth.errors.generic");
  });

  it("reports connectivity problems as a network error", () => {
    expect(errorKey(new NetworkError())).toBe("Auth.errors.network");
    expect(errorKey(new CsrfUnavailableError())).toBe("Auth.errors.network");
  });

  it("uses the generic message for anything else", () => {
    expect(errorKey(new Error("boom"))).toBe("Auth.errors.generic");
    expect(errorKey("boom")).toBe("Auth.errors.generic");
  });
});

describe("validationIssueKey", () => {
  const expectations: Record<ValidationIssue, string> = {
    EMAIL_INVALID: "Auth.errors.invalidEmail",
    PASSWORD_REQUIRED: "Auth.errors.passwordRequired",
    PASSWORD_TOO_SHORT: "Auth.errors.passwordTooShort",
    PASSWORD_MISMATCH: "Auth.errors.passwordMismatch",
    NAME_REQUIRED: "Auth.errors.nameRequired",
    NAME_TOO_LONG: "Auth.errors.nameTooLong",
    PHONE_INVALID: "Auth.errors.phoneInvalid",
    MUNICIPALITY_REQUIRED: "Auth.errors.municipalityRequired",
  };

  it.each(Object.entries(expectations))("maps %s", (issue, expected) => {
    expect(validationIssueKey(issue as ValidationIssue)).toBe(expected);
  });
});
