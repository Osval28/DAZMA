import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

const { useSessionProfile } = vi.hoisted(() => ({ useSessionProfile: vi.fn() }));

vi.mock("@/components/account/useSessionProfile", () => ({ useSessionProfile }));
vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header data-testid="site-header" /> }));
vi.mock("./ChatRoomList", () => ({ ChatRoomList: () => <p>lista de chats</p> }));

import { ChatListView } from "./ChatListView";

describe("ChatListView", () => {
  beforeEach(() => {
    useSessionProfile.mockReset();
  });

  it("asks to come back to the chat list after signing in", () => {
    useSessionProfile.mockReturnValue({ state: { status: "loading" } });

    renderWithIntl(<ChatListView />);

    expect(useSessionProfile).toHaveBeenCalledWith("/chats");
    expect(screen.getByText(t("Account.loading"))).toBeInTheDocument();
    expect(screen.queryByText("lista de chats")).not.toBeInTheDocument();
  });

  it("shows the failure", () => {
    useSessionProfile.mockReturnValue({ state: { status: "failed", errorKey: "Auth.errors.network" } });

    renderWithIntl(<ChatListView />);

    expect(screen.getByRole("alert")).toHaveTextContent(t("Auth.errors.network"));
  });

  it("shows the list for a registered user", () => {
    useSessionProfile.mockReturnValue({ state: { status: "ready", profile: { id: "u1" } } });

    renderWithIntl(<ChatListView />);

    expect(screen.getByText("lista de chats")).toBeInTheDocument();
    expect(screen.getByTestId("site-header")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Chats.room.back") })).toHaveAttribute("href", "/");
  });
});
