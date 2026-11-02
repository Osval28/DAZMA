export function chatPath(roomId: string): string {
  return `/chats/${encodeURIComponent(roomId)}`;
}
