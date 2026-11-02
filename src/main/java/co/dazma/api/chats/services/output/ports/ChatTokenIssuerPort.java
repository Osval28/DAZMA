package co.dazma.api.chats.services.output.ports;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;

public interface ChatTokenIssuerPort {

    ChatTokenRequest issue(ChatRoom room, ChatParticipant participant);
}
