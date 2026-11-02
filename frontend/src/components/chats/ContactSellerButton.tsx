"use client";

import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { ChatErrorKey, AuthErrorKey } from "@/i18n/message-keys";
import { openChatRoom } from "@/lib/api/chats";
import { isUnauthenticated } from "@/lib/api/errors";
import { LOGIN_PATH } from "@/lib/auth/sign-in";
import { chatPath } from "@/lib/chats/paths";
import { describeChatError } from "@/lib/chats/errors";
import { publicationPath } from "@/lib/publications/paths";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";

type ContactSellerButtonProps = {
  publicationId: string;
};

export function ContactSellerButton({ publicationId }: ContactSellerButtonProps) {
  const t = useTranslations();
  const router = useRouter();
  const [opening, setOpening] = useState(false);
  const [failure, setFailure] = useState<ChatErrorKey | AuthErrorKey>();

  async function handleClick() {
    setOpening(true);
    setFailure(undefined);
    try {
      const room = await openChatRoom(publicationId);
      router.push(chatPath(room.id));
    } catch (error) {
      if (isUnauthenticated(error)) {
        router.replace(`${LOGIN_PATH}?next=${encodeURIComponent(publicationPath(publicationId))}`);
        return;
      }
      setFailure(describeChatError(error));
      setOpening(false);
    }
  }

  return (
    <div className="detail__contact">
      {failure !== undefined && <Alert>{t(failure)}</Alert>}
      <Button variant="secondary" onClick={handleClick} disabled={opening}>
        {opening ? t("Chats.contact.opening") : t("Chats.contact.button")}
      </Button>
    </div>
  );
}
