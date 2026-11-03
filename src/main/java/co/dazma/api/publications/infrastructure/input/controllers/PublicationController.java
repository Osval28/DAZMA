package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.shared.infrastructure.security.CurrentUser;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationRequestDto;
import co.dazma.api.publications.infrastructure.input.dto.CreatePublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.GetPublicationResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.ListPublicationsResponseDto;
import co.dazma.api.publications.infrastructure.input.dto.PublicationRulesResponseDto;
import co.dazma.api.publications.infrastructure.input.mapper.PublicationWebMapper;
import co.dazma.api.publications.services.input.dto.CreatePublicationInputDto;
import co.dazma.api.publications.services.input.dto.CreatePublicationOutputDto;
import co.dazma.api.publications.services.input.dto.ListPublicationsInputDto;
import co.dazma.api.publications.services.input.ports.CreatePublicationInputPort;
import co.dazma.api.publications.services.input.ports.GetPublicationInputPort;
import co.dazma.api.publications.services.input.ports.GetPublicationRulesInputPort;
import co.dazma.api.publications.services.input.ports.ListPublicationsInputPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(PublicationController.PATH)
public class PublicationController {

    public static final String PATH = "/publications/api/v1/publications";

    private final CreatePublicationInputPort createPublicationInputPort;
    private final GetPublicationInputPort getPublicationInputPort;
    private final ListPublicationsInputPort listPublicationsInputPort;
    private final PublicationWebMapper publicationWebMapper;
    private final GetPublicationRulesInputPort getPublicationRulesInputPort;

    public PublicationController(
            CreatePublicationInputPort createPublicationInputPort,
            GetPublicationInputPort getPublicationInputPort,
            ListPublicationsInputPort listPublicationsInputPort,
            PublicationWebMapper publicationWebMapper,
            GetPublicationRulesInputPort getPublicationRulesInputPort
    ) {
        this.createPublicationInputPort = createPublicationInputPort;
        this.getPublicationInputPort = getPublicationInputPort;
        this.listPublicationsInputPort = listPublicationsInputPort;
        this.publicationWebMapper = publicationWebMapper;
        this.getPublicationRulesInputPort = getPublicationRulesInputPort;
    }

    @PostMapping
    public ResponseEntity<CreatePublicationResponseDto> create(
            @RequestBody @Valid CreatePublicationRequestDto request,
            @AuthenticationPrincipal CurrentUser currentUser,
            UriComponentsBuilder uriBuilder
    ) {
        CreatePublicationOutputDto publication = createPublicationInputPort.execute(
                new CreatePublicationInputDto(
                        currentUser.uid(),
                        currentUser.emailVerified(),
                        request.name(),
                        publicationWebMapper.toDomain(request.type()),
                        request.description(),
                        request.municipality(),
                        request.price(),
                        request.photoUploadIds()
                )
        );

        URI location = uriBuilder
                .path(PATH + "/{publicationId}")
                .buildAndExpand(publication.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(publicationWebMapper.toResponse(publication));
    }

    @GetMapping
    public ListPublicationsResponseDto list(
            @RequestParam Optional<String> municipality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam Optional<Integer> size
    ) {
        return publicationWebMapper.toResponse(
                listPublicationsInputPort.execute(new ListPublicationsInputDto(municipality, page, size))
        );
    }

    @GetMapping("/rules")
    public ResponseEntity<PublicationRulesResponseDto> rules() {
        var rules = getPublicationRulesInputPort.execute();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new PublicationRulesResponseDto(
                rules.maximumPhotos(),
                rules.maximumPageSize(),
                rules.defaultPageSize(),
                rules.allowedPhotoContentTypes(),
                rules.maximumPhotoBytes()
        ));
    }

    @GetMapping("/{publicationId}")
    public ResponseEntity<GetPublicationResponseDto> getById(@PathVariable UUID publicationId) {
        return ResponseEntity.ok(
                publicationWebMapper.toResponse(getPublicationInputPort.execute(publicationId))
        );
    }
}
