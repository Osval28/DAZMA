import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, NetworkError } from "@/lib/api/errors";
import { renderWithIntl, t } from "@/test/intl";

const mocks = vi.hoisted(() => {
  const push = vi.fn();
  const replace = vi.fn();
  return { push, replace, router: { push, replace }, openChatRoom: vi.fn() };
});

vi.mock("next/navigation", () => ({ useRouter: () => mocks.router }));
vi.mock("@/lib/api/chats", () => ({ openChatRoom: mocks.openChatRoom }));

import { ContactSellerButton } from "./ContactSellerButton";

describe("ContactSellerButton", () => {
  beforeEach(() => {
    mocks.push.mockReset();
    mocks.replace.mockReset();
    mocks.openChatRoom.mockReset();
  });

  it("opens the room of the publication and goes to it", async () => {
    mocks.openChatRoom.mockResolvedValue({ id: "room-1" });
    renderWithIntl(<ContactSellerButton publicationId="pub-1" />);

    await userEvent.click(screen.getByRole("button", { name: t("Chats.contact.button") }));

    await waitFor(() => expect(mocks.push).toHaveBeenCalledWith("/chats/room-1"));
    expect(mocks.openChatRoom).toHaveBeenCalledWith("pub-1");
  });

  it("sends visitors without session to sign in and back to the publication", async () => {
    mocks.openChatRoom.mockRejectedValue(new ApiError(401, "AUTHENTICATION_REQUIRED"));
    renderWithIntl(<ContactSellerButton publicationId="pub-1" />);

    await userEvent.click(screen.getByRole("button"));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/ingresar?next=%2Fpublicaciones%2Fpub-1"));
  });

  it("explains a chat that is not ready yet and lets the user retry", async () => {
    mocks.openChatRoom.mockRejectedValue(new ApiError(409, "CHAT_PARTICIPANT_NOT_READY"));
    renderWithIntl(<ContactSellerButton publicationId="pub-1" />);

    await userEvent.click(screen.getByRole("button"));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Chats.errors.notReady"));
    expect(screen.getByRole("button", { name: t("Chats.contact.button") })).toBeEnabled();
    expect(mocks.push).not.toHaveBeenCalled();
  });

  it("explains that the email must be verified", async () => {
    mocks.openChatRoom.mockRejectedValue(new ApiError(403, "CHAT_EMAIL_NOT_VERIFIED"));
    renderWithIntl(<ContactSellerButton publicationId="pub-1" />);

    await userEvent.click(screen.getByRole("button"));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Chats.errors.emailNotVerified"));
  });

  it("explains that nobody can chat about their own publication", async () => {
    mocks.openChatRoom.mockRejectedValue(new ApiError(403, "CHAT_SELF_NOT_ALLOWED"));
    renderWithIntl(<ContactSellerButton publicationId="pub-1" />);

    await userEvent.click(screen.getByRole("button"));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Chats.errors.selfNotAllowed"));
  });

  it("reports connectivity problems", async () => {
    mocks.openChatRoom.mockRejectedValue(new NetworkError());
    renderWithIntl(<ContactSellerButton publicationId="pub-1" />);

    await userEvent.click(screen.getByRole("button"));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Auth.errors.network"));
  });

  it("is disabled while opening", async () => {
    mocks.openChatRoom.mockReturnValue(new Promise(() => undefined));
    renderWithIntl(<ContactSellerButton publicationId="pub-1" />);

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByRole("button", { name: t("Chats.contact.opening") })).toBeDisabled();
  });
});
