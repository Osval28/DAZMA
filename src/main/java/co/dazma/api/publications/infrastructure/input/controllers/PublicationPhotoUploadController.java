package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.infrastructure.input.dto.PublicationPhotoUploadAuthorizationResponseDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadUseCase;
import co.dazma.api.users.services.input.ports.ResolveUserIdUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/publications/photos")
@ConditionalOnProperty(
        prefix = "dazma.storage.r2",
        name = "enabled",
        havingValue = "true"
)
public class PublicationPhotoUploadController {

    private final ResolveUserIdUseCase resolveUserIdUseCase;
    private final AuthorizePublicationPhotoUploadUseCase authorizeUploadUseCase;

    public PublicationPhotoUploadController(
            ResolveUserIdUseCase resolveUserIdUseCase,
            AuthorizePublicationPhotoUploadUseCase authorizeUploadUseCase
    ) {
        this.resolveUserIdUseCase = resolveUserIdUseCase;
        this.authorizeUploadUseCase = authorizeUploadUseCase;
    }

    @PostMapping("/upload-authorizations")
    @ResponseStatus(HttpStatus.CREATED)
    public PublicationPhotoUploadAuthorizationResponseDto authorizeUpload(
            @RequestBody co.dazma.api.publications.infrastructure.input.dto.AuthorizePublicationPhotoUploadRequestDto request,
            Principal principal
    ) {
        UUID ownerId = resolveUserIdUseCase.resolveByExternalId(principal.getName());
        AuthorizePublicationPhotoUploadOutputDto authorization = authorizeUploadUseCase.authorize(
                new AuthorizePublicationPhotoUploadInputDto(
                        ownerId,
                        request.contentType(),
                        request.contentLength()
                )
        );

        return PublicationPhotoUploadAuthorizationResponseDto.fromModel(authorization);
    }
}
