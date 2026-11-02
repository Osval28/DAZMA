import { describe, expect, it } from "vitest";
import { CHATS_PATH, chatPath } from "./paths";

describe("chat paths", () => {
  it("points to the chat list", () => {
    expect(CHATS_PATH).toBe("/chats");
  });

  it("builds the path of a room", () => {
    expect(chatPath("room-1")).toBe("/chats/room-1");
  });

  it("encodes the room id", () => {
    expect(chatPath("a/b?c")).toBe("/chats/a%2Fb%3Fc");
  });
});
