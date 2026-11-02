package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatParticipantNotReadyException;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import co.dazma.api.chats.services.usecases.contract.ListChatRoomsUseCase;
import co.dazma.api.chats.services.usecases.models.ChatRoomSummary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListChatRoomsService implements ListChatRoomsUseCase {

    private final OperatingParticipantResolver participants;
    private final ChatRoomRepositoryPort roomRepository;
    private final ChatParticipantRepositoryPort participantRepository;

    public ListChatRoomsService(
            OperatingParticipantResolver participants,
            ChatRoomRepositoryPort roomRepository,
            ChatParticipantRepositoryPort participantRepository
    ) {
        this.participants = participants;
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public List<ChatRoomSummary> execute(String externalId) {
        UUID userId = participants.resolve(externalId).userId();
        List<ChatRoom> rooms = roomRepository.findByParticipant(userId);
        Map<UUID, String> namesById = namesOfTheOtherParticipants(rooms, userId);

        return rooms.stream()
                .map(room -> new ChatRoomSummary(room.getId(), nameOf(namesById, room.otherParticipantOf(userId))))
                .toList();
    }

    private Map<UUID, String> namesOfTheOtherParticipants(List<ChatRoom> rooms, UUID userId) {
        List<UUID> otherIds = rooms.stream().map(room -> room.otherParticipantOf(userId)).distinct().toList();
        return participantRepository.findAllByUserIds(otherIds).stream()
                .collect(Collectors.toMap(ChatParticipant::userId, ChatParticipant::name));
    }

    private static String nameOf(Map<UUID, String> namesById, UUID userId) {
        String name = namesById.get(userId);
        if (name == null) {
            throw new ChatParticipantNotReadyException();
        }
        return name;
    }
}
