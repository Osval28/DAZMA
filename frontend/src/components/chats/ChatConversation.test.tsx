import { render, screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

const mocks = vi.hoisted(() => ({
  connect: vi.fn(),
  close: vi.fn(),
  createChatConnection: vi.fn(),
}));

vi.mock("@/lib/chats/client", () => ({ createChatConnection: mocks.createChatConnection }));
vi.mock("@ably/chat/react", () => ({
  ChatClientProvider: ({ client, children }: { client: { id: string }; children: React.ReactNode }) => (
    <div data-testid="client" data-client={client.id}>
      {children}
    </div>
  ),
  ChatRoomProvider: ({ name, children }: { name: string; children: React.ReactNode }) => (
    <div data-testid="room" data-name={name}>
      {children}
    </div>
  ),
}));
vi.mock("./MessageThread", () => ({ MessageThread: ({ userId }: { userId: string }) => <p>hilo de {userId}</p> }));

import { ChatConversation } from "./ChatConversation";

describe("ChatConversation", () => {
  beforeEach(() => {
    mocks.connect.mockReset();
    mocks.close.mockReset();
    mocks.createChatConnection.mockReset();
    mocks.createChatConnection.mockReturnValue({ chatClient: { id: "client-1" }, realtime: { connect: mocks.connect, close: mocks.close } });
  });

  it("connects to the room of the conversation", () => {
    render(<ChatConversation roomId="room-1" userId="user-9" />);

    expect(mocks.createChatConnection).toHaveBeenCalledWith("room-1");
    expect(screen.getByTestId("client")).toHaveAttribute("data-client", "client-1");
    expect(screen.getByTestId("room")).toHaveAttribute("data-name", "dazma-chat:room-1");
    expect(screen.getByText("hilo de user-9")).toBeInTheDocument();
  });

  it("opens the connection on mount and closes it on unmount", () => {
    const { unmount } = render(<ChatConversation roomId="room-1" userId="user-9" />);

    expect(mocks.connect).toHaveBeenCalledTimes(1);
    expect(mocks.close).not.toHaveBeenCalled();

    unmount();

    expect(mocks.close).toHaveBeenCalledTimes(1);
  });

  it("creates the connection only once while it stays mounted", () => {
    const { rerender } = render(<ChatConversation roomId="room-1" userId="user-9" />);

    rerender(<ChatConversation roomId="room-1" userId="user-9" />);

    expect(mocks.createChatConnection).toHaveBeenCalledTimes(1);
  });
});
