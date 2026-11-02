"use client";

import { ChatClientProvider, ChatRoomProvider } from "@ably/chat/react";
import { useEffect, useState } from "react";
import { createChatConnection } from "@/lib/chats/client";
import { chatRoomName } from "@/lib/chats/room-name";
import { MessageThread } from "./MessageThread";

type ChatConversationProps = {
  roomId: string;
  userId: string;
};

export function ChatConversation({ roomId, userId }: ChatConversationProps) {
  const [connection] = useState(() => createChatConnection(roomId));

  useEffect(() => {
    connection.realtime.connect();
    return () => {
      connection.realtime.close();
    };
  }, [connection]);

  return (
    <ChatClientProvider client={connection.chatClient}>
      <ChatRoomProvider name={chatRoomName(roomId)}>
        <MessageThread userId={userId} />
      </ChatRoomProvider>
    </ChatClientProvider>
  );
}
