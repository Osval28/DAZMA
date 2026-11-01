import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import { AccountView } from "@/components/account/AccountView";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Metadata.pages");
  return { title: t("account") };
}

export default function AccountPage() {
  return <AccountView />;
}
