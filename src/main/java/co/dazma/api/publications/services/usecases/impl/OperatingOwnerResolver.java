package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.domain.PublicationOwnerCannotOperateException;
import co.dazma.api.publications.domain.PublicationOwnerNotReadyException;
import co.dazma.api.publications.services.output.ports.PublicationOwnerRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OperatingOwnerResolver {

    private final PublicationOwnerRepositoryPort ownerRepository;

    public OperatingOwnerResolver(PublicationOwnerRepositoryPort ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public UUID resolveId(String externalId) {
        PublicationOwner owner = ownerRepository.findByExternalId(externalId)
                .orElseThrow(PublicationOwnerNotReadyException::new);
        if (!owner.canOperate()) {
            throw new PublicationOwnerCannotOperateException();
        }
        return owner.userId();
    }

    public String resolveName(String externalId) {
        PublicationOwner owner = ownerRepository.findByExternalId(externalId)
                .orElseThrow(PublicationOwnerNotReadyException::new);
        if (!owner.canOperate()) {
            throw new PublicationOwnerCannotOperateException();
        }
        return owner.userName();
    }


}
