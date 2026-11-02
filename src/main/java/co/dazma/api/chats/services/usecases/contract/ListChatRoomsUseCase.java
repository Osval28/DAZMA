package co.dazma.api.chats.services.usecases.contract;

import co.dazma.api.chats.services.usecases.models.ChatRoomSummary;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

import java.util.List;

public interface ListChatRoomsUseCase extends UseCaseWithReturn<String, List<ChatRoomSummary>> {
}
