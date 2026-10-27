package co.dazma.api.security.services.input.adapters.mappers;

import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.TokenVerification;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityInputDto;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityOutputDto;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;

public class AuthenticatedIdentityMapper implements
        InputMapper<AuthenticateIdentityInputDto, TokenVerification>,
        OutputMapper<AuthenticatedIdentity, AuthenticateIdentityOutputDto> {

    @Override
    public TokenVerification toDomain(AuthenticateIdentityInputDto input) {
        return new TokenVerification(input.token(), input.checkRevoked());
    }

    @Override
    public AuthenticateIdentityOutputDto toOutput(AuthenticatedIdentity identity) {
        return new AuthenticateIdentityOutputDto(identity.uid(), identity.email(), identity.emailVerified());
    }
}
