import { accountMessages } from "@/lib/account/messages";
import { toneOf } from "@/lib/account/avatar-tone";
import { initialOf } from "@/lib/account/initial";

type AvatarProps = {
  name: string | undefined;
  size?: "regular" | "medium" | "large";
  tinted?: boolean;
};

export function Avatar({ name, size = "regular", tinted = false }: AvatarProps) {
  const initial = name === undefined ? accountMessages.header.unknownInitial : initialOf(name);
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
