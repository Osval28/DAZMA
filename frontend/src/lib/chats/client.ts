import { ChatClient } from "@ably/chat";
import * as Ably from "ably";
import { requestChatToken } from "@/lib/api/chats";

export type ChatConnection = {
  chatClient: ChatClient;
  realtime: Ably.Realtime;
};

export function createChatConnection(roomId: string): ChatConnection {
  const realtime = new Ably.Realtime({
    autoConnect: false,
    authCallback: (_parameters, callback) => {
      requestChatToken(roomId).then(
        (tokenRequest) => callback(null, tokenRequest),
        (error: unknown) => callback(error instanceof Error ? error.message : String(error), null),
      );
    },
  });
  return { chatClient: new ChatClient(realtime), realtime };
}
