import { beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("./http", () => ({ apiJson: vi.fn() }));

import { listChatRooms, openChatRoom, requestChatToken } from "./chats";
import { apiJson } from "./http";

describe("chat API", () => {
  beforeEach(() => {
    vi.mocked(apiJson).mockResolvedValue({});
  });

  it("lists the rooms of the user", async () => {
    await listChatRooms();

    expect(apiJson).toHaveBeenCalledWith("/chats/api/v1/rooms");
  });

  it("opens a room for a publication", async () => {
    await openChatRoom("pub-1");

    expect(apiJson).toHaveBeenCalledWith("/chats/api/v1/rooms", { method: "POST", body: { publicationId: "pub-1" } });
  });

  it("requests a token for a room and encodes its id", async () => {
    await requestChatToken("room/1");

    expect(apiJson).toHaveBeenCalledWith("/chats/api/v1/rooms/room%2F1/token", { method: "POST" });
  });
});
