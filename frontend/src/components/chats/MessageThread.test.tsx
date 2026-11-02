import { act, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

type ThreadMessage = { serial: string; clientId: string; text: string; timestamp: Date };
type MessageEvent = { type: string; message: ThreadMessage };

const mocks = vi.hoisted(() => ({
  sendMessage: vi.fn(),
  historyBeforeSubscribe: vi.fn(),
  listener: undefined as ((event: MessageEvent) => void) | undefined,
  withHistory: true,
}));

vi.mock("@ably/chat", () => ({ ChatMessageEventType: { Created: "message.created", Deleted: "message.deleted" } }));
vi.mock("@ably/chat/react", () => ({
  useMessages: (options: { listener: (event: MessageEvent) => void }) => {
    mocks.listener = options.listener;
    return { sendMessage: mocks.sendMessage, historyBeforeSubscribe: mocks.withHistory ? mocks.historyBeforeSubscribe : undefined };
  },
}));

import { MessageThread } from "./MessageThread";

function message(serial: string, clientId: string, text: string, minute: number): ThreadMessage {
  return { serial, clientId, text, timestamp: new Date(2026, 10, 1, 10, minute) };
}

function deliver(event: MessageEvent) {
  act(() => mocks.listener?.(event));
}

describe("MessageThread", () => {
  beforeEach(() => {
    mocks.sendMessage.mockReset();
    mocks.historyBeforeSubscribe.mockReset();
    mocks.historyBeforeSubscribe.mockResolvedValue({ items: [] });
    mocks.withHistory = true;
    mocks.listener = undefined;
  });

  it("invites to write the first message", async () => {
    renderWithIntl(<MessageThread userId="me" />);

    expect(await screen.findByText(t("Chats.room.empty"))).toBeInTheDocument();
    expect(mocks.historyBeforeSubscribe).toHaveBeenCalledWith({ limit: 50 });
  });

  it("shows the history and tells my messages from theirs", async () => {
    mocks.historyBeforeSubscribe.mockResolvedValue({ items: [message("2", "me", "hola", 1), message("1", "other", "buenas", 0)] });

    renderWithIntl(<MessageThread userId="me" />);

    const mine = await screen.findByText("hola");
    const theirs = screen.getByText("buenas");
    expect(mine.closest(".chat__message")).toHaveClass("chat__message--mine");
    expect(theirs.closest(".chat__message")).toHaveClass("chat__message--theirs");
    expect(screen.queryByText(t("Chats.room.empty"))).not.toBeInTheDocument();
  });

  it("orders the messages by time", async () => {
    mocks.historyBeforeSubscribe.mockResolvedValue({ items: [message("2", "me", "segundo", 5), message("1", "other", "primero", 1)] });

    renderWithIntl(<MessageThread userId="me" />);

    await screen.findByText("primero");
    const texts = Array.from(document.querySelectorAll(".chat__text")).map((node) => node.textContent);
    expect(texts).toEqual(["primero", "segundo"]);
  });

  it("appends the messages that arrive live", async () => {
    renderWithIntl(<MessageThread userId="me" />);
    await screen.findByText(t("Chats.room.empty"));

    deliver({ type: "message.created", message: message("9", "other", "llegó ahora", 3) });

    expect(await screen.findByText("llegó ahora")).toBeInTheDocument();
  });

  it("does not repeat a message that is already shown", async () => {
    mocks.historyBeforeSubscribe.mockResolvedValue({ items: [message("1", "other", "hola", 0)] });
    renderWithIntl(<MessageThread userId="me" />);
    await screen.findByText("hola");

    deliver({ type: "message.created", message: message("1", "other", "hola", 0) });

    expect(screen.getAllByText("hola")).toHaveLength(1);
  });

  it("ignores events that are not new messages", async () => {
    renderWithIntl(<MessageThread userId="me" />);
    await screen.findByText(t("Chats.room.empty"));

    deliver({ type: "message.deleted", message: message("1", "other", "borrado", 0) });

    expect(screen.queryByText("borrado")).not.toBeInTheDocument();
  });

  it("sends the trimmed text and clears the box", async () => {
    mocks.sendMessage.mockResolvedValue(undefined);
    renderWithIntl(<MessageThread userId="me" />);

    await userEvent.type(screen.getByLabelText(t("Chats.room.messageLabel")), "  hola  ");
    await userEvent.click(screen.getByRole("button", { name: t("Chats.room.send") }));

    await waitFor(() => expect(mocks.sendMessage).toHaveBeenCalledWith({ text: "hola" }));
    await waitFor(() => expect(screen.getByLabelText(t("Chats.room.messageLabel"))).toHaveValue(""));
  });

  it("sends with the Enter key", async () => {
    mocks.sendMessage.mockResolvedValue(undefined);
    renderWithIntl(<MessageThread userId="me" />);

    await userEvent.type(screen.getByLabelText(t("Chats.room.messageLabel")), "hola{Enter}");

    await waitFor(() => expect(mocks.sendMessage).toHaveBeenCalledWith({ text: "hola" }));
  });

  it("cannot send an empty or blank message", async () => {
    renderWithIntl(<MessageThread userId="me" />);
    const send = screen.getByRole("button", { name: t("Chats.room.send") });
    expect(send).toBeDisabled();

    await userEvent.type(screen.getByLabelText(t("Chats.room.messageLabel")), "   ");

    expect(send).toBeDisabled();
    expect(mocks.sendMessage).not.toHaveBeenCalled();
  });

  it("keeps the draft and reports a failed send", async () => {
    mocks.sendMessage.mockRejectedValue(new Error("offline"));
    renderWithIntl(<MessageThread userId="me" />);

    await userEvent.type(screen.getByLabelText(t("Chats.room.messageLabel")), "hola");
    await userEvent.click(screen.getByRole("button", { name: t("Chats.room.send") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Chats.room.failed"));
    expect(screen.getByLabelText(t("Chats.room.messageLabel"))).toHaveValue("hola");
    expect(screen.getByRole("button", { name: t("Chats.room.send") })).toBeEnabled();
  });

  it("is disabled while sending", async () => {
    mocks.sendMessage.mockReturnValue(new Promise(() => undefined));
    renderWithIntl(<MessageThread userId="me" />);

    await userEvent.type(screen.getByLabelText(t("Chats.room.messageLabel")), "hola");
    await userEvent.click(screen.getByRole("button", { name: t("Chats.room.send") }));

    expect(screen.getByRole("button", { name: t("Chats.room.sending") })).toBeDisabled();
  });

  it("reports when the history cannot be loaded", async () => {
    mocks.historyBeforeSubscribe.mockRejectedValue(new Error("offline"));

    renderWithIntl(<MessageThread userId="me" />);

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Chats.room.failed"));
  });

  it("does not ask for history before the room is ready", () => {
    mocks.withHistory = false;

    renderWithIntl(<MessageThread userId="me" />);

    expect(mocks.historyBeforeSubscribe).not.toHaveBeenCalled();
    expect(screen.getByText(t("Chats.room.empty"))).toBeInTheDocument();
  });
});
