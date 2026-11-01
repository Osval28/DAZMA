import { accountMessages } from "@/lib/account/messages";
import { initialOf } from "@/lib/account/initial";

type AvatarProps = {
  name: string | undefined;
  size?: "regular" | "large";
};

export function Avatar({ name, size = "regular" }: AvatarProps) {
  const initial = name === undefined ? accountMessages.header.unknownInitial : initialOf(name);

  return (
    <span className={`avatar avatar--${size}`} aria-hidden="true">
      {initial}
    </span>
  );
}
