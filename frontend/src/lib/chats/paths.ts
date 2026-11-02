export const CHATS_PATH = "/chats";

export function chatPath(roomId: string): string {
  return `/chats/${encodeURIComponent(roomId)}`;
}
