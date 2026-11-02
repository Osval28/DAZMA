package co.dazma.api.chats.infrastructure.input.controllers;

import co.dazma.api.chats.infrastructure.input.dto.ChatTokenResponseDto;
import co.dazma.api.chats.infrastructure.input.dto.OpenChatRoomRequestDto;
import co.dazma.api.chats.infrastructure.input.dto.OpenChatRoomResponseDto;
import co.dazma.api.chats.services.input.dto.IssueChatTokenInputDto;
import co.dazma.api.chats.services.input.dto.OpenChatRoomInputDto;
import co.dazma.api.chats.services.input.ports.IssueChatTokenInputPort;
import co.dazma.api.chats.services.input.ports.OpenChatRoomInputPort;
import co.dazma.api.shared.infrastructure.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ChatRoomController.PATH)
public class ChatRoomController {

    public static final String PATH = "/chats/api/v1/rooms";

    private final OpenChatRoomInputPort openChatRoomInputPort;
    private final IssueChatTokenInputPort issueChatTokenInputPort;

    public ChatRoomController(
            OpenChatRoomInputPort openChatRoomInputPort,
            IssueChatTokenInputPort issueChatTokenInputPort
    ) {
        this.openChatRoomInputPort = openChatRoomInputPort;
        this.issueChatTokenInputPort = issueChatTokenInputPort;
    }

    @PostMapping
    public OpenChatRoomResponseDto open(
            @RequestBody @Valid OpenChatRoomRequestDto request,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return OpenChatRoomResponseDto.from(openChatRoomInputPort.execute(new OpenChatRoomInputDto(
                currentUser.uid(),
                currentUser.emailVerified(),
                request.publicationId()
        )));
    }

    @PostMapping("/{roomId}/token")
    public ChatTokenResponseDto issueToken(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ChatTokenResponseDto.from(issueChatTokenInputPort.execute(new IssueChatTokenInputDto(
                currentUser.uid(),
                roomId
        )));
    }
}
