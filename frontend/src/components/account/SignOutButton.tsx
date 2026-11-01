"use client";

import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import { LOGIN_PATH, signOutCompletely } from "@/lib/auth/sign-in";
import { Button } from "@/components/ui/Button";

export function SignOutButton() {
  const t = useTranslations("Account.account");
  const router = useRouter();

  async function handleClick() {
    try {
      await signOutCompletely();
    } finally {
      router.replace(LOGIN_PATH);
    }
  }

  return (
    <Button variant="secondary" onClick={handleClick}>
      {t("signOut")}
    </Button>
  );
}
