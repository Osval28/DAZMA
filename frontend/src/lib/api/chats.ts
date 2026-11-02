import { apiJson } from "./http";

export type ChatRoom = {
  id: string;
};

export type ChatTokenRequest = {
  keyName: string;
  ttl: number;
  timestamp: number;
  capability: string;
  clientId: string;
  nonce: string;
  mac: string;
};

const ROOMS_PATH = "/chats/api/v1/rooms";

export function openChatRoom(publicationId: string): Promise<ChatRoom> {
  return apiJson<ChatRoom>(ROOMS_PATH, { method: "POST", body: { publicationId } });
}

export function requestChatToken(roomId: string): Promise<ChatTokenRequest> {
  return apiJson<ChatTokenRequest>(`${ROOMS_PATH}/${encodeURIComponent(roomId)}/token`, { method: "POST" });
}
