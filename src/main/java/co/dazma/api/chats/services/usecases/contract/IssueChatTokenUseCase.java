package co.dazma.api.chats.services.usecases.contract;

import co.dazma.api.chats.infrastructure.input.dto.ChatAccessRequest;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import co.dazma.api.shared.services.usecases.UseCaseWithReturn;

public interface IssueChatTokenUseCase extends UseCaseWithReturn<ChatAccessRequest, ChatTokenRequest> {
}
