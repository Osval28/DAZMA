import type { Metadata } from "next";
import { AccountView } from "@/components/account/AccountView";

export const metadata: Metadata = {
  title: "Mi cuenta | DAZMA",
};

export default function AccountPage() {
  return <AccountView />;
}
