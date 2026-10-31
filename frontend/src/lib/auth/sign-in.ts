import { FirebaseError } from "firebase/app";
import {
  GoogleAuthProvider,
  createUserWithEmailAndPassword,
  sendEmailVerification,
  signInWithEmailAndPassword,
  signInWithPopup,
  signOut,
} from "firebase/auth";
import { hasErrorCode } from "@/lib/api/errors";
import { closeSession, openSession } from "@/lib/api/session";
import { getMyProfile } from "@/lib/api/users";
import { getFirebaseAuth } from "@/lib/firebase/auth-client";

export const HOME_PATH = "/";
export const ACCOUNT_PATH = "/cuenta";
export const LOGIN_PATH = "/ingresar";
export const REGISTER_PATH = "/registro";

const USER_NOT_FOUND_CODE = "USER_NOT_FOUND";
const DISMISSED_POPUP_CODES = ["auth/popup-closed-by-user", "auth/cancelled-popup-request"];

export async function destinationAfterSignIn(): Promise<string> {
  try {
    await getMyProfile();
    return HOME_PATH;
  } catch (error) {
    if (hasErrorCode(error, USER_NOT_FOUND_CODE)) {
      return ACCOUNT_PATH;
    }
    throw error;
  }
}

export async function signInWithEmail(email: string, password: string): Promise<string> {
  const credential = await signInWithEmailAndPassword(getFirebaseAuth(), email, password);
  await openSession(await credential.user.getIdToken());
  return destinationAfterSignIn();
}

export async function signInWithGoogle(): Promise<string> {
  const credential = await signInWithPopup(getFirebaseAuth(), new GoogleAuthProvider());
  await openSession(await credential.user.getIdToken());
  return destinationAfterSignIn();
}

export function isPopupDismissed(error: unknown): boolean {
  return error instanceof FirebaseError && DISMISSED_POPUP_CODES.includes(error.code);
}

export async function createAccount(email: string, password: string): Promise<void> {
  const credential = await createUserWithEmailAndPassword(getFirebaseAuth(), email, password);
  await sendEmailVerification(credential.user);
  await openSession(await credential.user.getIdToken());
}

export async function signOutCompletely(): Promise<void> {
  await closeSession();
  await signOut(getFirebaseAuth());
}
