import { getApp, getApps, initializeApp } from "firebase/app";
import { browserPopupRedirectResolver, inMemoryPersistence, initializeAuth, type Auth } from "firebase/auth";
import { config } from "@/lib/config";

let firebaseAuth: Auth | undefined;

export function getFirebaseAuth(): Auth {
  if (firebaseAuth === undefined) {
    const firebaseApp = getApps().length > 0 ? getApp() : initializeApp(config.firebase);
    firebaseAuth = initializeAuth(firebaseApp, {
      persistence: inMemoryPersistence,
      popupRedirectResolver: browserPopupRedirectResolver,
    });
  }
  return firebaseAuth;
}