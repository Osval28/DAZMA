import { FirebaseError } from "firebase/app";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { ApiError, CsrfUnavailableError, NetworkError } from "@/lib/api/errors";
import type { ValidationIssue } from "@/lib/validation";

const GENERIC_ERROR: AuthErrorKey = "Auth.errors.generic";
const NETWORK_ERROR: AuthErrorKey = "Auth.errors.network";

const firebaseErrors: Record<string, AuthErrorKey> = {
  "auth/invalid-credential": "Auth.errors.invalidCredentials",
  "auth/invalid-email": "Auth.errors.invalidEmail",
  "auth/user-disabled": "Auth.errors.userDisabled",
  "auth/too-many-requests": "Auth.errors.tooManyRequests",
  "auth/email-already-in-use": "Auth.errors.emailInUse",
  "auth/weak-password": "Auth.errors.weakPassword",
  "auth/popup-blocked": "Auth.errors.popupBlocked",
  "auth/account-exists-with-different-credential": "Auth.errors.differentProvider",
  "auth/network-request-failed": NETWORK_ERROR,
};

const apiErrors: Record<string, AuthErrorKey> = {
  AUTHENTICATION_REQUIRED: "Auth.errors.sessionExpired",
  IDENTITY_TOKEN_INVALID: "Auth.errors.sessionExpired",
  RECENT_SIGN_IN_REQUIRED: "Auth.errors.recentSignInRequired",
  ORIGIN_NOT_ALLOWED: "Auth.errors.originNotAllowed",
  USER_ALREADY_REGISTERED: "Auth.errors.alreadyRegistered",
  MUNICIPALITY_NOT_FOUND: "Auth.errors.municipalityNotFound",
  USER_PHONE_INVALID: "Auth.errors.phoneInvalid",
  USER_PHONE_REQUIRED: "Auth.errors.phoneInvalid",
  USER_NAME_REQUIRED: "Auth.errors.nameRequired",
  USER_NAME_TOO_LONG: "Auth.errors.nameTooLong",
};

const validationErrors: Record<ValidationIssue, AuthErrorKey> = {
  EMAIL_INVALID: "Auth.errors.invalidEmail",
  PASSWORD_REQUIRED: "Auth.errors.passwordRequired",
  PASSWORD_TOO_SHORT: "Auth.errors.passwordTooShort",
  PASSWORD_MISMATCH: "Auth.errors.passwordMismatch",
  NAME_REQUIRED: "Auth.errors.nameRequired",
  NAME_TOO_LONG: "Auth.errors.nameTooLong",
  PHONE_INVALID: "Auth.errors.phoneInvalid",
  MUNICIPALITY_REQUIRED: "Auth.errors.municipalityRequired",
};

export function validationIssueKey(issue: ValidationIssue): AuthErrorKey {
  return validationErrors[issue];
}

export function errorKey(error: unknown): AuthErrorKey {
  if (error instanceof FirebaseError) {
    return firebaseErrors[error.code] ?? GENERIC_ERROR;
  }
  if (error instanceof ApiError) {
    return apiErrors[error.code] ?? GENERIC_ERROR;
  }
  if (error instanceof NetworkError || error instanceof CsrfUnavailableError) {
    return NETWORK_ERROR;
  }
  return GENERIC_ERROR;
}
