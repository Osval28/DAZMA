package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.services.input.dto.PublicationRulesOutputDto;
import co.dazma.api.publications.services.input.ports.GetPublicationRulesInputPort;
import co.dazma.api.publications.services.output.ports.PhotoUploadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationRulesPort;
import org.springframework.stereotype.Service;

@Service
public class GetPublicationRulesInputAdapter implements GetPublicationRulesInputPort {

    private final PublicationRulesPort publicationRules;
    private final PhotoUploadParametersPort photoUploadParameters;

    public GetPublicationRulesInputAdapter(
            PublicationRulesPort publicationRules,
            PhotoUploadParametersPort photoUploadParameters
    ) {
        this.publicationRules = publicationRules;
        this.photoUploadParameters = photoUploadParameters;
    }

    @Override
    public PublicationRulesOutputDto execute() {
        var rules = publicationRules.current();
        return new PublicationRulesOutputDto(
                rules.maximumPhotos(),
                rules.maximumPageSize(),
                rules.defaultPageSize(),
                photoUploadParameters.allowedContentTypes(),
                photoUploadParameters.maximumContentLength()
        );
    }
}
