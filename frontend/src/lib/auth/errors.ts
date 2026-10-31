import { FirebaseError } from "firebase/app";
import { ApiError, CsrfUnavailableError, NetworkError } from "@/lib/api/errors";
import type { ValidationIssue } from "@/lib/validation";
import { authMessages } from "./messages";

const messages = authMessages.errors;

const firebaseMessages: Record<string, string> = {
  "auth/invalid-credential": messages.invalidCredentials,
  "auth/invalid-email": messages.invalidEmail,
  "auth/user-disabled": messages.userDisabled,
  "auth/too-many-requests": messages.tooManyRequests,
  "auth/email-already-in-use": messages.emailInUse,
  "auth/weak-password": messages.weakPassword,
  "auth/popup-blocked": messages.popupBlocked,
  "auth/account-exists-with-different-credential": messages.differentProvider,
  "auth/network-request-failed": messages.network,
};

const apiMessages: Record<string, string> = {
  AUTHENTICATION_REQUIRED: messages.sessionExpired,
  IDENTITY_TOKEN_INVALID: messages.sessionExpired,
  RECENT_SIGN_IN_REQUIRED: messages.recentSignInRequired,
  ORIGIN_NOT_ALLOWED: messages.originNotAllowed,
  USER_ALREADY_REGISTERED: messages.alreadyRegistered,
  MUNICIPALITY_NOT_FOUND: messages.municipalityNotFound,
  USER_PHONE_INVALID: messages.phoneInvalid,
  USER_PHONE_REQUIRED: messages.phoneInvalid,
  USER_NAME_REQUIRED: messages.nameRequired,
  USER_NAME_TOO_LONG: messages.nameTooLong,
};

const validationMessages: Record<ValidationIssue, string> = {
  EMAIL_INVALID: messages.invalidEmail,
  PASSWORD_REQUIRED: messages.passwordRequired,
  PASSWORD_TOO_SHORT: messages.passwordTooShort,
  PASSWORD_MISMATCH: messages.passwordMismatch,
  NAME_REQUIRED: messages.nameRequired,
  NAME_TOO_LONG: messages.nameTooLong,
  PHONE_INVALID: messages.phoneInvalid,
  MUNICIPALITY_REQUIRED: messages.municipalityRequired,
};

export function describeValidationIssue(issue: ValidationIssue): string {
  return validationMessages[issue];
}

export function describeError(error: unknown): string {
  if (error instanceof FirebaseError) {
    return firebaseMessages[error.code] ?? messages.generic;
  }
  if (error instanceof ApiError) {
    return apiMessages[error.code] ?? messages.generic;
  }
  if (error instanceof NetworkError || error instanceof CsrfUnavailableError) {
    return messages.network;
  }
  return messages.generic;
}
