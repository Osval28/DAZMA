package co.dazma.api.publications.services.output.ports;

import java.time.Duration;
import java.util.Set;

public interface PhotoUploadParametersPort {

    Set<String> allowedContentTypes();

    long maximumContentLength();

    Duration uploadValidity();
}
