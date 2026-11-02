import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, NetworkError } from "@/lib/api/errors";
import { renderWithIntl, t } from "@/test/intl";

const { listChatRooms } = vi.hoisted(() => ({ listChatRooms: vi.fn() }));

vi.mock("@/lib/api/chats", () => ({ listChatRooms }));

import { ChatRoomList } from "./ChatRoomList";

describe("ChatRoomList", () => {
  beforeEach(() => {
    listChatRooms.mockReset();
  });

  it("shows a loading message first", () => {
    listChatRooms.mockReturnValue(new Promise(() => undefined));

    renderWithIntl(<ChatRoomList />);

    expect(screen.getByText(t("Chats.list.loading"))).toBeInTheDocument();
  });

  it("lists each conversation by the name of the other person", async () => {
    listChatRooms.mockResolvedValue([
      { id: "room-1", participantName: "Luis" },
      { id: "room-2", participantName: "Marta" },
    ]);

    renderWithIntl(<ChatRoomList />);

    const first = await screen.findByRole("link", { name: /Luis/ });
    expect(first).toHaveAttribute("href", "/chats/room-1");
    expect(screen.getByRole("link", { name: /Marta/ })).toHaveAttribute("href", "/chats/room-2");
    expect(screen.getAllByRole("listitem")).toHaveLength(2);
  });

  it("explains when there are no conversations", async () => {
    listChatRooms.mockResolvedValue([]);

    renderWithIntl(<ChatRoomList />);

    expect(await screen.findByText(t("Chats.list.empty"))).toBeInTheDocument();
    expect(screen.queryByRole("list")).not.toBeInTheDocument();
  });

  it("explains that the account is not ready for chat", async () => {
    listChatRooms.mockRejectedValue(new ApiError(409, "CHAT_PARTICIPANT_NOT_READY"));

    renderWithIntl(<ChatRoomList />);

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Chats.errors.notReady"));
  });

  it("explains that a suspended account cannot chat", async () => {
    listChatRooms.mockRejectedValue(new ApiError(403, "CHAT_PARTICIPANT_CANNOT_OPERATE"));

    renderWithIntl(<ChatRoomList />);

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Chats.errors.cannotOperate"));
  });

  it("reports connectivity problems", async () => {
    listChatRooms.mockRejectedValue(new NetworkError());

    renderWithIntl(<ChatRoomList />);

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.network"));
  });
});
