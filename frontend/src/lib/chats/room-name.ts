const ROOM_NAMESPACE = "dazma-chat";

export function chatRoomName(roomId: string): string {
  return `${ROOM_NAMESPACE}:${roomId}`;
}
