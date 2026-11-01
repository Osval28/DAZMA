import { useTranslations } from "next-intl";
import { toneOf } from "@/lib/account/avatar-tone";
import { initialOf } from "@/lib/account/initial";

type AvatarProps = {
  name: string | undefined;
  size?: "regular" | "medium" | "large";
  tinted?: boolean;
};

export function Avatar({ name, size = "regular", tinted = false }: AvatarProps) {
  const t = useTranslations("Account.header");
  const initial = name === undefined ? t("unknownInitial") : initialOf(name);
  const classes = ["avatar", `avatar--${size}`];
  if (tinted && name !== undefined) {
    classes.push(`avatar--tone-${toneOf(name)}`);
  }

  return (
    <span className={classes.join(" ")} aria-hidden="true">
      {initial}
    </span>
  );
}
