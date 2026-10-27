package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.services.output.ports.PublicationOwnerRepositoryPort;
import co.dazma.api.publications.services.usecases.contract.RegisterPublicationOwnerUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterPublicationOwnerService implements RegisterPublicationOwnerUseCase {

    private final PublicationOwnerRepositoryPort ownerRepository;

    public RegisterPublicationOwnerService(PublicationOwnerRepositoryPort ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public void execute(PublicationOwner owner) {
        ownerRepository.save(owner);
    }
}
