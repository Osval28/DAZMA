package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.domain.Publication;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationRequestDto;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationResponseDto;
import co.dazma.api.publications.services.input.models.CreatePublicationCommand;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.users.services.input.ports.ResolveUserIdUseCase;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/publications")
@ConditionalOnProperty(
        prefix = "dazma.storage.r2",
        name = "enabled",
        havingValue = "true"
)
public class PublicationController {

    private final ResolveUserIdUseCase resolveUserIdUseCase;
    private final CreatePublicationUseCase createPublicationUseCase;

    public PublicationController(
            ResolveUserIdUseCase resolveUserIdUseCase,
            CreatePublicationUseCase createPublicationUseCase
    ) {
        this.resolveUserIdUseCase = resolveUserIdUseCase;
        this.createPublicationUseCase = createPublicationUseCase;
    }

    @PostMapping
    public ResponseEntity<CreatePublicationResponseDto> create(
            @RequestBody @Valid CreatePublicationRequestDto request,
            Principal principal,
            UriComponentsBuilder uriBuilder
    ) {
        UUID ownerId = resolveUserIdUseCase.resolveByExternalId(principal.getName());
        Publication publication = createPublicationUseCase.create(
                new CreatePublicationCommand(
                        ownerId,
                        request.name(),
                        request.type(),
                        request.description(),
                        request.municipality(),
                        request.price(),
                        request.photoUploadIds()
                )
        );

        URI location = uriBuilder
                .path("/api/publications/{publicationId}")
                .buildAndExpand(publication.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(CreatePublicationResponseDto.fromDomain(publication));
    }
}
