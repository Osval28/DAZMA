import { getApp, getApps, initializeApp } from "firebase/app";
import { browserPopupRedirectResolver, inMemoryPersistence, initializeAuth } from "firebase/auth";
import { config } from "@/lib/config";

const firebaseApp = getApps().length > 0 ? getApp() : initializeApp(config.firebase);

export const firebaseAuth = initializeAuth(firebaseApp, {
  persistence: inMemoryPersistence,
  popupRedirectResolver: browserPopupRedirectResolver,
});
