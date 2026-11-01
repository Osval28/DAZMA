import Link from "next/link";
import { useTranslations } from "next-intl";
import { ACCOUNT_PATH } from "@/lib/auth/sign-in";
import { Avatar } from "./Avatar";

type UserBadgeProps = {
  name: string | undefined;
};

export function UserBadge({ name }: UserBadgeProps) {
  const t = useTranslations("Account.header");
  const label = name ?? t("account");

  return (
    <Link href={ACCOUNT_PATH} className="user-badge" aria-label={t("accountOf", { name: label })}>
      <Avatar name={name} />
      <span className="user-badge__name">{label}</span>
    </Link>
  );
}
