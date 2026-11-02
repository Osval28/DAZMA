package co.dazma.api.chats.domain;

import co.dazma.api.shared.domain.errors.DomainNotFoundException;

import java.util.UUID;

public class ChatRoomNotFoundException extends DomainNotFoundException {

    public ChatRoomNotFoundException(UUID roomId) {
        super(ChatErrorCode.ROOM_NOT_FOUND, roomId);
    }
}
