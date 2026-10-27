package co.dazma.api.publications.infrastructure.input.controllers;

import co.dazma.api.publications.infrastructure.input.dto.AuthorizePublicationPhotoUploadRequestDto;
import co.dazma.api.publications.infrastructure.input.dto.PublicationPhotoUploadAuthorizationResponseDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadInputDto;
import co.dazma.api.publications.services.input.dto.AuthorizePublicationPhotoUploadOutputDto;
import co.dazma.api.publications.services.input.ports.AuthorizePublicationPhotoUploadInputPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/publications/photos")
public class PublicationPhotoUploadController {

    private final AuthorizePublicationPhotoUploadInputPort authorizeUploadInputPort;

    public PublicationPhotoUploadController(
            AuthorizePublicationPhotoUploadInputPort authorizeUploadInputPort
    ) {
        this.authorizeUploadInputPort = authorizeUploadInputPort;
    }

    @PostMapping("/upload-authorizations")
    @ResponseStatus(HttpStatus.CREATED)
    public PublicationPhotoUploadAuthorizationResponseDto authorizeUpload(
            @RequestBody @Valid AuthorizePublicationPhotoUploadRequestDto request,
            Principal principal
    ) {
        AuthorizePublicationPhotoUploadOutputDto authorization = authorizeUploadInputPort.execute(
                new AuthorizePublicationPhotoUploadInputDto(
                        principal.getName(),
                        request.contentType(),
                        request.contentLength()
                )
        );

        return PublicationPhotoUploadAuthorizationResponseDto.fromModel(authorization);
    }
}
