import { describe, expect, it } from "vitest";
import { chatRoomName } from "./room-name";

describe("chatRoomName", () => {
  it("prefixes the room id with the chat namespace", () => {
    expect(chatRoomName("room-1")).toBe("dazma-chat:room-1");
  });
});
