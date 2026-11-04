package co.dazma.api.chats.services.output.ports;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.models.ChatRoom;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;

public interface ChatTokenIssuerPort {

    ChatTokenRequest issue(ChatRoom room, ChatParticipant participant);
}
