"use client";

import { ChatMessageEventType, type Message } from "@ably/chat";
import { useMessages } from "@ably/chat/react";
import { useTranslations } from "next-intl";
import { useEffect, useRef, useState } from "react";
import type { FormEvent } from "react";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { formatMessageTime } from "@/lib/chats/format-time";

const HISTORY_LIMIT = 50;
const MAXIMUM_MESSAGE_LENGTH = 1000;

type MessageThreadProps = {
  userId: string;
};

function appendMessage(messages: Message[], incoming: Message): Message[] {
  if (messages.some((message) => message.serial === incoming.serial)) {
    return messages;
  }
  return [...messages, incoming].sort((first, second) => first.timestamp.getTime() - second.timestamp.getTime());
}

export function MessageThread({ userId }: MessageThreadProps) {
  const t = useTranslations("Chats.room");
  const [messages, setMessages] = useState<Message[]>([]);
  const [draft, setDraft] = useState("");
  const [sending, setSending] = useState(false);
  const [failed, setFailed] = useState(false);
  const endOfThread = useRef<HTMLDivElement>(null);

  const { sendMessage, historyBeforeSubscribe } = useMessages({
    listener: (event) => {
      if (event.type === ChatMessageEventType.Created) {
        setMessages((current) => appendMessage(current, event.message));
      }
    },
  });

  useEffect(() => {
    if (historyBeforeSubscribe === undefined) {
      return;
    }
    let active = true;
    historyBeforeSubscribe({ limit: HISTORY_LIMIT })
      .then((page) => {
        if (active) {
          setMessages((current) => page.items.reduce(appendMessage, current));
        }
      })
      .catch(() => active && setFailed(true));
    return () => {
      active = false;
    };
  }, [historyBeforeSubscribe]);

  useEffect(() => {
    endOfThread.current?.scrollIntoView({ block: "end" });
  }, [messages.length]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const text = draft.trim();
    if (text === "") {
      return;
    }
    setSending(true);
    setFailed(false);
    try {
      await sendMessage({ text });
      setDraft("");
    } catch {
      setFailed(true);
    } finally {
      setSending(false);
    }
  }

  return (
    <section className="chat__card" aria-label={t("title")}>
      <h1 className="chat__title">{t("title")}</h1>
      <div className="chat__thread" role="log" aria-live="polite" aria-label={t("threadLabel")}>
        {messages.length === 0 && <p className="chat__empty">{t("empty")}</p>}
        {messages.map((message) => {
          const mine = message.clientId === userId;
          return (
            <div key={message.serial} className={`chat__message ${mine ? "chat__message--mine" : "chat__message--theirs"}`}>
              <p className="chat__text">{message.text}</p>
              <span className="chat__time">{formatMessageTime(message.timestamp)}</span>
            </div>
          );
        })}
        <div ref={endOfThread} />
      </div>
      {failed && <Alert>{t("failed")}</Alert>}
      <form className="chat__composer" onSubmit={handleSubmit}>
        <label className="visually-hidden" htmlFor="chat-message">
          {t("messageLabel")}
        </label>
        <input
          id="chat-message"
          className="chat__input"
          value={draft}
          maxLength={MAXIMUM_MESSAGE_LENGTH}
          onChange={(event) => setDraft(event.target.value)}
          placeholder={t("messagePlaceholder")}
          autoComplete="off"
        />
        <Button type="submit" disabled={sending || draft.trim() === ""}>
          {sending ? t("sending") : t("send")}
        </Button>
      </form>
    </section>
  );
}
