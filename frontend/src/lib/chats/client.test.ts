import { beforeEach, describe, expect, it, vi } from "vitest";

const { requestChatToken } = vi.hoisted(() => ({ requestChatToken: vi.fn() }));

vi.mock("ably", () => ({
  Realtime: class Realtime {
    constructor(public readonly options: Record<string, unknown>) {}
  },
}));
vi.mock("@ably/chat", () => ({
  ChatClient: class ChatClient {
    constructor(public readonly realtime: unknown) {}
  },
}));
vi.mock("@/lib/api/chats", () => ({ requestChatToken }));

import { createChatConnection } from "./client";

type AuthCallback = (parameters: unknown, callback: (error: string | null, token: unknown) => void) => void;

function authCallbackOf(roomId: string): AuthCallback {
  const { realtime } = createChatConnection(roomId);
  return (realtime as unknown as { options: { authCallback: AuthCallback } }).options.authCallback;
}

describe("createChatConnection", () => {
  beforeEach(() => {
    requestChatToken.mockReset();
  });

  it("creates a realtime client that connects only when asked", () => {
    const { realtime } = createChatConnection("room-1");

    expect((realtime as unknown as { options: { autoConnect: boolean } }).options.autoConnect).toBe(false);
  });

  it("wraps the realtime client in a chat client", () => {
    const { chatClient, realtime } = createChatConnection("room-1");

    expect((chatClient as unknown as { realtime: unknown }).realtime).toBe(realtime);
  });

  it("hands the signed token request of the room to Ably", async () => {
    const tokenRequest = { keyName: "key", mac: "mac" };
    requestChatToken.mockResolvedValue(tokenRequest);
    const callback = vi.fn();

    authCallbackOf("room-1")({}, callback);

    await vi.waitFor(() => expect(callback).toHaveBeenCalledWith(null, tokenRequest));
    expect(requestChatToken).toHaveBeenCalledWith("room-1");
  });

  it("reports a stable error code for a failed token request", async () => {
    requestChatToken.mockRejectedValue(new Error("no token"));
    const callback = vi.fn();

    authCallbackOf("room-1")({}, callback);

    await vi.waitFor(() => expect(callback).toHaveBeenCalledWith("CHAT_TOKEN_UNAVAILABLE", null));
  });

  it("does not expose an arbitrary failure as text", async () => {
    requestChatToken.mockRejectedValue("denied");
    const callback = vi.fn();

    authCallbackOf("room-1")({}, callback);

    await vi.waitFor(() => expect(callback).toHaveBeenCalledWith("CHAT_TOKEN_UNAVAILABLE", null));
  });
});
