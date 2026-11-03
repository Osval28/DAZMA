package co.dazma.api.chats.infrastructure.input.controllers;

import co.dazma.api.chats.domain.exceptions.ChatRoomNotFoundException;
import co.dazma.api.chats.services.input.dto.ChatRoomSummaryOutputDto;
import co.dazma.api.chats.services.input.dto.IssueChatTokenInputDto;
import co.dazma.api.chats.services.input.dto.IssueChatTokenOutputDto;
import co.dazma.api.chats.services.input.dto.ListChatRoomsInputDto;
import co.dazma.api.chats.services.input.dto.OpenChatRoomInputDto;
import co.dazma.api.chats.services.input.dto.OpenChatRoomOutputDto;
import co.dazma.api.chats.services.input.ports.IssueChatTokenInputPort;
import co.dazma.api.chats.services.input.ports.ListChatRoomsInputPort;
import co.dazma.api.chats.services.input.ports.OpenChatRoomInputPort;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import co.dazma.api.shared.infrastructure.input.errors.GlobalExceptionHandler;
import co.dazma.api.shared.infrastructure.security.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatRoomControllerTest {

    private OpenChatRoomInputPort openChatRoomInputPort;
    private IssueChatTokenInputPort issueChatTokenInputPort;
    private ListChatRoomsInputPort listChatRoomsInputPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        openChatRoomInputPort = mock(OpenChatRoomInputPort.class);
        issueChatTokenInputPort = mock(IssueChatTokenInputPort.class);
        listChatRoomsInputPort = mock(ListChatRoomsInputPort.class);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(
                new ApiProblemFactory(new ErrorMessageResolver(messageSource))
        );

        mockMvc = MockMvcBuilders.standaloneSetup(new ChatRoomController(openChatRoomInputPort, issueChatTokenInputPort, listChatRoomsInputPort))
                .setControllerAdvice(exceptionHandler)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        new CurrentUser("firebase-123", "ana@dazma.store", true),
                        null,
                        List.of()
                )
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldOpenARoomForTheAuthenticatedUser() throws Exception {
        UUID publicationId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        when(openChatRoomInputPort.execute(new OpenChatRoomInputDto("firebase-123", true, publicationId)))
                .thenReturn(new OpenChatRoomOutputDto(roomId));

        mockMvc.perform(post("/chats/api/v1/rooms")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publicationId\": \"%s\"}".formatted(publicationId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId.toString()));
    }

    @Test
    void shouldListTheRoomsOfTheAuthenticatedUser() throws Exception {
        UUID roomId = UUID.randomUUID();
        when(listChatRoomsInputPort.execute(new ListChatRoomsInputDto("firebase-123")))
                .thenReturn(List.of(new ChatRoomSummaryOutputDto(roomId, "Luis")));

        mockMvc.perform(get("/chats/api/v1/rooms").principal(() -> "firebase-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(roomId.toString()))
                .andExpect(jsonPath("$[0].participantName").value("Luis"));
    }

    @Test
    void shouldRejectARequestWithoutPublicationBeforeCallingTheUseCase() throws Exception {
        mockMvc.perform(post("/chats/api/v1/rooms")
                        .principal(() -> "firebase-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(openChatRoomInputPort, never()).execute(any());
    }

    @Test
    void shouldIssueATokenRequestForTheRoom() throws Exception {
        UUID roomId = UUID.randomUUID();
        when(issueChatTokenInputPort.execute(new IssueChatTokenInputDto("firebase-123", roomId))).thenReturn(
                new IssueChatTokenOutputDto("key", 3_600_000, 1_700_000_000_000L, "{}", "client", "nonce", "mac")
        );

        mockMvc.perform(post("/chats/api/v1/rooms/{roomId}/token", roomId).principal(() -> "firebase-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyName").value("key"))
                .andExpect(jsonPath("$.ttl").value(3_600_000))
                .andExpect(jsonPath("$.timestamp").value(1_700_000_000_000L))
                .andExpect(jsonPath("$.capability").value("{}"))
                .andExpect(jsonPath("$.clientId").value("client"))
                .andExpect(jsonPath("$.nonce").value("nonce"))
                .andExpect(jsonPath("$.mac").value("mac"));
    }

    @Test
    void shouldAnswerNotFoundWhenTheRoomIsNotAvailableToTheUser() throws Exception {
        UUID roomId = UUID.randomUUID();
        when(issueChatTokenInputPort.execute(new IssueChatTokenInputDto("firebase-123", roomId)))
                .thenThrow(new ChatRoomNotFoundException(roomId));

        mockMvc.perform(post("/chats/api/v1/rooms/{roomId}/token", roomId).principal(() -> "firebase-123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHAT_ROOM_NOT_FOUND"));
    }
}
