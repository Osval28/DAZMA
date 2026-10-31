package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatParticipantEntity;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatParticipantEntityJPA;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories.SpringDataChatParticipantRepository;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresqlChatParticipantAdapter implements ChatParticipantRepositoryPort {

    private final SpringDataChatParticipantRepository springDataParticipantRepository;
    private final DomainMapper<ChatParticipantEntity, ChatParticipant> domainMapper;
    private final JpaMapper<ChatParticipantEntityJPA, ChatParticipantEntity> jpaMapper;

    public PostgresqlChatParticipantAdapter(
            SpringDataChatParticipantRepository springDataParticipantRepository,
            DomainMapper<ChatParticipantEntity, ChatParticipant> domainMapper,
            JpaMapper<ChatParticipantEntityJPA, ChatParticipantEntity> jpaMapper
    ) {
        this.springDataParticipantRepository = springDataParticipantRepository;
        this.domainMapper = domainMapper;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public void save(ChatParticipant participant) {
        springDataParticipantRepository.save(jpaMapper.toJpaEntity(domainMapper.toEntity(participant)));
    }

    @Override
    public Optional<ChatParticipant> findByExternalId(String externalId) {
        return springDataParticipantRepository.findByExternalId(externalId)
                .map(jpaMapper::toEntity)
                .map(domainMapper::toDomain);
    }
}