package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationRequestDto;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.GetPublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.mapper.PublicationWebMapper;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.input.ports.CreatePublicationUseCase;
import co.dazma.api.publications.services.input.ports.GetPublicationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class PublicationController {

    private final CreatePublicationUseCase createPublicationUseCase;
    private final GetPublicationUseCase getPublicationUseCase;
    private final PublicationWebMapper publicationWebMapper;

    public PublicationController(
            CreatePublicationUseCase createPublicationUseCase,
            GetPublicationUseCase getPublicationUseCase,
            PublicationWebMapper publicationWebMapper
    ) {
        this.createPublicationUseCase = createPublicationUseCase;
        this.getPublicationUseCase = getPublicationUseCase;
        this.publicationWebMapper = publicationWebMapper;
    }

    @PostMapping
    public ResponseEntity<CreatePublicationResponseDto> create(
            @RequestBody @Valid CreatePublicationRequestDto request,
            Principal principal,
            UriComponentsBuilder uriBuilder
    ) {
        CreatePublicationOutputDto publication = createPublicationUseCase.create(
                new CreatePublicationInputDto(
                        principal.getName(),
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

    @GetMapping("/{publicationId}")
    public ResponseEntity<GetPublicationResponseDto> getById(@PathVariable UUID publicationId) {
        return ResponseEntity.ok(
                publicationWebMapper.toResponse(getPublicationUseCase.getById(publicationId))
        );
    }
}
