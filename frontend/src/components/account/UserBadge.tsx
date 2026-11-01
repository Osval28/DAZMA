import Link from "next/link";
import { accountMessages } from "@/lib/account/messages";
import { ACCOUNT_PATH } from "@/lib/auth/sign-in";
import { Avatar } from "./Avatar";

const messages = accountMessages.header;

type UserBadgeProps = {
  name: string | undefined;
};

export function UserBadge({ name }: UserBadgeProps) {
  const label = name ?? messages.account;

  return (
    <Link href={ACCOUNT_PATH} className="user-badge" aria-label={`${messages.account}: ${label}`}>
      <Avatar name={name} />
      <span className="user-badge__name">{label}</span>
    </Link>
  );
}
