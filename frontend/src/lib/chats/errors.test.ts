import { describe, expect, it } from "vitest";
import { ApiError, NetworkError } from "@/lib/api/errors";
import { describeChatError } from "./errors";

describe("describeChatError", () => {
  it.each([
    ["CHAT_EMAIL_NOT_VERIFIED", "Chats.errors.emailNotVerified"],
    ["CHAT_SELF_NOT_ALLOWED", "Chats.errors.selfNotAllowed"],
    ["CHAT_PARTICIPANT_CANNOT_OPERATE", "Chats.errors.cannotOperate"],
    ["CHAT_PARTICIPANT_NOT_READY", "Chats.errors.notReady"],
    ["CHAT_OFFER_NOT_FOUND", "Chats.errors.notReady"],
    ["CHAT_ROOM_NOT_FOUND", "Chats.errors.roomNotFound"],
    ["CHAT_TOKEN_ISSUANCE_FAILED", "Chats.errors.generic"],
  ])("maps the chat code %s", (code, expected) => {
    expect(describeChatError(new ApiError(409, code))).toBe(expected);
  });

  it("falls back to the authentication errors for other API codes", () => {
    expect(describeChatError(new ApiError(401, "AUTHENTICATION_REQUIRED"))).toBe("Auth.errors.sessionExpired");
  });

  it("falls back to the authentication errors for connectivity problems", () => {
    expect(describeChatError(new NetworkError())).toBe("Auth.errors.network");
  });

  it("uses the generic authentication message for anything unknown", () => {
    expect(describeChatError(new Error("boom"))).toBe("Auth.errors.generic");
  });
});
