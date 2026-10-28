package co.dazma.api.security.services.input.adapters.mappers;

import co.dazma.api.security.domain.Session;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.shared.services.input.mappers.OutputMapper;

public class SessionMapper implements OutputMapper<Session, OpenSessionOutputDto> {

    @Override
    public OpenSessionOutputDto toOutput(Session session) {
        return new OpenSessionOutputDto(session.token(), session.validity());
    }
}
