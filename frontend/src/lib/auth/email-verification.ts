import { openSession } from "@/lib/api/session";
import { getFirebaseAuth } from "@/lib/firebase/auth-client";

export async function refreshVerifiedSession(): Promise<boolean> {
  const user = getFirebaseAuth().currentUser;
  if (user === null) {
    return false;
  }
  await user.reload();
  await openSession(await user.getIdToken(true));
  return true;
}
