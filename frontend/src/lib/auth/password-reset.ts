import { FirebaseError } from "firebase/app";
import { sendPasswordResetEmail } from "firebase/auth";
import { getFirebaseAuth } from "@/lib/firebase/auth-client";

export const PASSWORD_RESET_PATH = "/recuperar-contrasena";

const UNKNOWN_ACCOUNT_CODE = "auth/user-not-found";

export async function requestPasswordReset(email: string): Promise<void> {
  try {
    await sendPasswordResetEmail(getFirebaseAuth(), email);
  } catch (error) {
    if (error instanceof FirebaseError && error.code === UNKNOWN_ACCOUNT_CODE) {
      return;
    }
    throw error;
  }
}
