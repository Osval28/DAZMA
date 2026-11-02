import type { AuthErrorKey, ChatErrorKey } from "@/i18n/message-keys";
import { ApiError } from "@/lib/api/errors";
import { errorKey } from "@/lib/auth/errors";

const chatErrors: Record<string, ChatErrorKey> = {
  CHAT_EMAIL_NOT_VERIFIED: "Chats.errors.emailNotVerified",
  CHAT_SELF_NOT_ALLOWED: "Chats.errors.selfNotAllowed",
  CHAT_PARTICIPANT_CANNOT_OPERATE: "Chats.errors.cannotOperate",
  CHAT_PARTICIPANT_NOT_READY: "Chats.errors.notReady",
  CHAT_OFFER_NOT_FOUND: "Chats.errors.notReady",
  CHAT_ROOM_NOT_FOUND: "Chats.errors.roomNotFound",
  CHAT_TOKEN_ISSUANCE_FAILED: "Chats.errors.generic",
};

export function describeChatError(error: unknown): ChatErrorKey | AuthErrorKey {
  if (error instanceof ApiError && error.code in chatErrors) {
    return chatErrors[error.code];
  }
  return errorKey(error);
}
