"use client";

import Link from "next/link";
import { useTranslations } from "next-intl";
import { useEffect, useState } from "react";
import type { AuthErrorKey, ChatErrorKey } from "@/i18n/message-keys";
import { listChatRooms, type ChatRoomSummary } from "@/lib/api/chats";
import { describeChatError } from "@/lib/chats/errors";
import { chatPath } from "@/lib/chats/paths";
import { Avatar } from "@/components/account/Avatar";
import { Alert } from "@/components/ui/Alert";

type ListState =
  | { status: "loading" }
  | { status: "ready"; rooms: ChatRoomSummary[] }
  | { status: "failed"; errorKey: ChatErrorKey | AuthErrorKey };

export function ChatRoomList() {
  const t = useTranslations("Chats.list");
  const tErrors = useTranslations();
  const [state, setState] = useState<ListState>({ status: "loading" });

  useEffect(() => {
    let active = true;
    listChatRooms().then(
      (rooms) => active && setState({ status: "ready", rooms }),
      (error: unknown) => active && setState({ status: "failed", errorKey: describeChatError(error) }),
    );
    return () => {
      active = false;
    };
  }, []);

  return (
    <section className="chat__card" aria-label={t("title")}>
      <h1 className="chat__title">{t("title")}</h1>
      {state.status === "loading" && <p className="page__status">{t("loading")}</p>}
      {state.status === "failed" && <Alert>{tErrors(state.errorKey)}</Alert>}
      {state.status === "ready" && state.rooms.length === 0 && <p className="chat__empty">{t("empty")}</p>}
      {state.status === "ready" && state.rooms.length > 0 && (
        <ul className="chat-list">
          {state.rooms.map((room) => (
            <li key={room.id}>
              <Link href={chatPath(room.id)} className="chat-list__item">
                <Avatar name={room.participantName} />
                <span className="chat-list__name">{room.participantName}</span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
