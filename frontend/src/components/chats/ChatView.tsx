"use client";

import Link from "next/link";
import { useTranslations } from "next-intl";
import { HOME_PATH } from "@/lib/auth/sign-in";
import { chatPath } from "@/lib/chats/paths";
import { SiteHeader } from "@/components/account/SiteHeader";
import { useSessionProfile } from "@/components/account/useSessionProfile";
import { Alert } from "@/components/ui/Alert";
import { ChatConversation } from "./ChatConversation";

type ChatViewProps = {
  roomId: string;
};

export function ChatView({ roomId }: ChatViewProps) {
  const t = useTranslations();
  const { state } = useSessionProfile(chatPath(roomId));

  return (
    <>
      <SiteHeader />
      <main id="contenido" className="chat">
        <Link href={HOME_PATH} className="detail__back">
          {t("Chats.room.back")}
        </Link>
        {(state.status === "loading" || state.status === "missing") && <p className="page__status">{t("Account.loading")}</p>}
        {state.status === "failed" && <Alert>{t(state.errorKey)}</Alert>}
        {state.status === "ready" && <ChatConversation roomId={roomId} userId={state.profile.id} />}
      </main>
    </>
  );
}
