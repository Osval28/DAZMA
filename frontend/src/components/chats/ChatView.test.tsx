import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const { useSessionProfile } = vi.hoisted(() => ({ useSessionProfile: vi.fn() }));

vi.mock("@/components/account/useSessionProfile", () => ({ useSessionProfile }));
vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header /> }));
vi.mock("./ChatConversation", () => ({
  ChatConversation: ({ roomId, userId }: { roomId: string; userId: string }) => <p>conversación {roomId} de {userId}</p>,
}));

import { ChatView } from "./ChatView";

describe("ChatView", () => {
  beforeEach(() => {
    useSessionProfile.mockReset();
  });

  it("asks to come back to the room after signing in", () => {
    useSessionProfile.mockReturnValue({ state: { status: "loading" } });

    renderWithIntl(<ChatView roomId="room 1" />);

    expect(useSessionProfile).toHaveBeenCalledWith("/chats/room%201");
    expect(screen.getByText(t("Account.loading"))).toBeInTheDocument();
  });

  it("waits while the profile is missing", () => {
    useSessionProfile.mockReturnValue({ state: { status: "missing" } });

    renderWithIntl(<ChatView roomId="room-1" />);

    expect(screen.getByText(t("Account.loading"))).toBeInTheDocument();
  });

  it("shows the failure", () => {
    useSessionProfile.mockReturnValue({ state: { status: "failed", errorKey: "Auth.errors.generic" } });

    renderWithIntl(<ChatView roomId="room-1" />);

    expect(screen.getByRole("alert")).toHaveTextContent(t("Auth.errors.generic"));
  });

  it("opens the conversation as the signed-in user", () => {
    useSessionProfile.mockReturnValue({ state: { status: "ready", profile: { id: "user-9" } } });

    renderWithIntl(<ChatView roomId="room-1" />);

    expect(screen.getByText("conversación room-1 de user-9")).toBeInTheDocument();
  });
});
