import Link from "next/link";
import { accountMessages } from "@/lib/account/messages";
import { initialOf } from "@/lib/account/initial";
import { ACCOUNT_PATH } from "@/lib/auth/sign-in";

const messages = accountMessages.header;

type UserBadgeProps = {
  name: string | undefined;
};

export function UserBadge({ name }: UserBadgeProps) {
  const label = name ?? messages.account;
  const initial = name === undefined ? messages.unknownInitial : initialOf(name);

  return (
    <Link href={ACCOUNT_PATH} className="user-badge" aria-label={`${messages.account}: ${label}`}>
      <span className="user-badge__avatar" aria-hidden="true">
        {initial}
      </span>
      <span className="user-badge__name">{label}</span>
    </Link>
  );
}
