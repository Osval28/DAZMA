package co.dazma.api.users.services.input.ports;

import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

import java.util.UUID;

public interface ResolveUserIdInputPort extends InputPortWithReturn<String, UUID> {
}
