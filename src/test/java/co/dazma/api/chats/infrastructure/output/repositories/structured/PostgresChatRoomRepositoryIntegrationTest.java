package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.TestcontainersConfiguration;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatRoomEntityJPA;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories.SpringDataChatRoomRepository;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PostgresChatRoomRepositoryIntegrationTest {

    private static final Instant OLDER = Instant.parse("2026-10-27T10:00:00Z");
    private static final Instant NEWER = Instant.parse("2026-10-27T11:00:00Z");

    @Autowired
    private ChatRoomRepositoryPort chatRoomRepository;

    @Autowired
    private SpringDataChatRoomRepository springDataRepository;

    @Test
    void shouldPersistAndFindAChatRoomByPublicationAndInterested() {
        ChatRoom chatRoom = ChatRoom.open(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), OLDER);

        chatRoomRepository.save(chatRoom);
        Optional<ChatRoom> found = chatRoomRepository.findByPublicationAndInterested(
                chatRoom.getPublicationId(),
                chatRoom.getInterestedId()
        );

        assertTrue(found.isPresent());
        assertEquals(chatRoom.getId(), found.get().getId());
        assertEquals(chatRoom.getOwnerId(), found.get().getOwnerId());
        assertEquals(OLDER, found.get().getCreatedAt());
    }

    @Test
    void shouldReturnEmptyWhenTheChatRoomDoesNotExist() {
        assertTrue(chatRoomRepository
                .findByPublicationAndInterested(UUID.randomUUID(), UUID.randomUUID())
                .isEmpty());
    }

    @Test
    void shouldFindTheRoomsOfAParticipantNewestFirst() {
        UUID userId = UUID.randomUUID();
        ChatRoom asOwner = ChatRoom.open(UUID.randomUUID(), userId, UUID.randomUUID(), OLDER);
        ChatRoom asInterested = ChatRoom.open(UUID.randomUUID(), UUID.randomUUID(), userId, NEWER);
        ChatRoom foreign = ChatRoom.open(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), NEWER);
        chatRoomRepository.save(asOwner);
        chatRoomRepository.save(asInterested);
        chatRoomRepository.save(foreign);

        List<ChatRoom> rooms = chatRoomRepository.findByParticipant(userId);

        assertEquals(List.of(asInterested, asOwner), rooms);
    }

    @Test
    void shouldRejectASecondRoomForTheSamePublicationAndInterested() {
        UUID publicationId = UUID.randomUUID();
        UUID interestedId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        springDataRepository.saveAndFlush(
                new ChatRoomEntityJPA(UUID.randomUUID(), publicationId, ownerId, interestedId, OLDER)
        );

        ChatRoomEntityJPA duplicated =
                new ChatRoomEntityJPA(UUID.randomUUID(), publicationId, ownerId, interestedId, NEWER);

        assertThrows(DataIntegrityViolationException.class, () -> springDataRepository.saveAndFlush(duplicated));
    }

    @Test
    void shouldRejectARoomWhereTheOwnerIsAlsoTheInterested() {
        UUID sameUser = UUID.randomUUID();
        ChatRoomEntityJPA selfChat =
                new ChatRoomEntityJPA(UUID.randomUUID(), UUID.randomUUID(), sameUser, sameUser, OLDER);

        assertThrows(DataIntegrityViolationException.class, () -> springDataRepository.saveAndFlush(selfChat));
    }
}