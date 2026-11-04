package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.chats.domain.models.ChatOffer;
import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatOfferEntity;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatOfferEntityJPA;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories.SpringDataChatOfferRepository;
import co.dazma.api.chats.services.output.ports.ChatOfferRepositoryPort;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresqlChatOfferAdapter implements ChatOfferRepositoryPort {

    private final SpringDataChatOfferRepository springDataOfferRepository;
    private final DomainMapper<ChatOfferEntity, ChatOffer> domainMapper;
    private final JpaMapper<ChatOfferEntityJPA, ChatOfferEntity> jpaMapper;

    public PostgresqlChatOfferAdapter(
            SpringDataChatOfferRepository springDataOfferRepository,
            DomainMapper<ChatOfferEntity, ChatOffer> domainMapper,
            JpaMapper<ChatOfferEntityJPA, ChatOfferEntity> jpaMapper
    ) {
        this.springDataOfferRepository = springDataOfferRepository;
        this.domainMapper = domainMapper;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public void save(ChatOffer offer) {
        springDataOfferRepository.save(jpaMapper.toJpaEntity(domainMapper.toEntity(offer)));
    }

    @Override
    public Optional<ChatOffer> findByPublicationId(UUID publicationId) {
        return springDataOfferRepository.findById(publicationId)
                .map(jpaMapper::toEntity)
                .map(domainMapper::toDomain);
    }
}
