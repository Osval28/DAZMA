package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationRequestDto;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.mapper.PublicationWebMapper;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
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
    private final PublicationWebMapper publicationWebMapper;

    public PublicationController(
            ResolveUserIdUseCase resolveUserIdUseCase,
            CreatePublicationUseCase createPublicationUseCase,
            PublicationWebMapper PublicationWebMapper
    ) {
        this.resolveUserIdUseCase = resolveUserIdUseCase;
        this.createPublicationUseCase = createPublicationUseCase;
        this.publicationWebMapper = PublicationWebMapper;
    }

    @PostMapping
    public ResponseEntity<CreatePublicationResponseDto> create(
            @RequestBody @Valid CreatePublicationRequestDto request,
            Principal principal,
            UriComponentsBuilder uriBuilder
    ) {
        UUID ownerId = resolveUserIdUseCase.resolveByExternalId(principal.getName());
        CreatePublicationOutputDto publication = createPublicationUseCase.create(
                new CreatePublicationInputDto(
                        ownerId,
                        request.name(),
                        publicationWebMapper.toDomain(request.type()),
                        request.description(),
                        request.municipality(),
                        request.price(),
                        request.photoUploadIds()
                )
        );

        URI location = uriBuilder
                .path("/api/publications/{publicationId}")
                .buildAndExpand(publication.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(publicationWebMapper.toResponse(publication));
    }
}
