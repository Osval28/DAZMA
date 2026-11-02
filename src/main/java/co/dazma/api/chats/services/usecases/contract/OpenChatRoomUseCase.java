package co.dazma.api.chats.services.usecases.contract;

import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.domain.ChatRoomOpening;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface OpenChatRoomUseCase extends UseCaseWithReturn<ChatRoomOpening, ChatRoom> {
}
