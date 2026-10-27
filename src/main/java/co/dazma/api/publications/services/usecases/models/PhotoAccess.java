package co.dazma.api.publications.services.usecases.models;

import java.net.URI;
import java.util.UUID;

public record PhotoAccess(UUID photoId, URI url, int position) {
}
