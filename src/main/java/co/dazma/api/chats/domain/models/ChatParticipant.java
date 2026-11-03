package co.dazma.api.chats.domain.models;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.domain.exceptions.InvalidChatParticipantException;

import java.util.UUID;

public record ChatParticipant(UUID userId, String externalId, String name, ChatParticipantState state) {

    public ChatParticipant {
        if (externalId.isBlank()) {
            throw new InvalidChatParticipantException(ChatErrorCode.PARTICIPANT_EXTERNAL_ID_REQUIRED);
        }
    }

    public boolean canOperate() {
        return state == ChatParticipantState.ACTIVE;
    }
}