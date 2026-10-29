package co.dazma.api.chats.domain;

import java.util.UUID;

public record ChatParticipant(UUID userId, String externalId, ChatParticipantState state) {

    public ChatParticipant {
        if (externalId.isBlank()) {
            throw new InvalidChatParticipantException(ChatErrorCode.PARTICIPANT_EXTERNAL_ID_REQUIRED);
        }
    }

    public boolean canOperate() {
        return state == ChatParticipantState.ACTIVE;
    }
}