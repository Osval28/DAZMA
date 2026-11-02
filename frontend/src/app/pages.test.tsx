import { screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";

vi.mock("next-intl/server", () => ({
  getTranslations: async (namespace: string) => (key: string) => `${namespace}.${key}`,
}));
vi.mock("@/i18n/actions", () => ({ setLocale: vi.fn() }));
vi.mock("@/components/chats/ChatView", () => ({ ChatView: ({ roomId }: { roomId: string }) => <p>chat {roomId}</p> }));
vi.mock("@/components/chats/ChatListView", () => ({ ChatListView: () => <p>lista de chats</p> }));
vi.mock("@/components/account/AccountView", () => ({ AccountView: () => <p>cuenta</p> }));
vi.mock("@/components/publications/NewPublicationView", () => ({ NewPublicationView: () => <p>nueva publicación</p> }));
vi.mock("@/components/auth/LoginForm", () => ({ LoginForm: () => <p>formulario de ingreso</p> }));
vi.mock("@/components/auth/RegisterForm", () => ({ RegisterForm: () => <p>formulario de registro</p> }));
vi.mock("@/components/auth/PasswordResetForm", () => ({ PasswordResetForm: () => <p>formulario de recuperación</p> }));

import ChatPage, { generateMetadata as chatMetadata } from "./chats/[id]/page";
import ChatsPage, { generateMetadata as chatsMetadata } from "./chats/page";
import AccountPage, { generateMetadata as accountMetadata } from "./cuenta/page";
import LoginPage, { generateMetadata as loginMetadata } from "./ingresar/page";
import NewPublicationPage, { generateMetadata as newPublicationMetadata } from "./publicaciones/nueva/page";
import PasswordResetPage, { generateMetadata as passwordResetMetadata } from "./recuperar-contrasena/page";
import RegisterPage, { generateMetadata as registerMetadata } from "./registro/page";

describe("page metadata", () => {
  it.each([
    ["chat", chatMetadata, "Metadata.pages.chat"],
    ["chat list", chatsMetadata, "Metadata.pages.chats"],
    ["account", accountMetadata, "Metadata.pages.account"],
    ["login", loginMetadata, "Metadata.pages.login"],
    ["new publication", newPublicationMetadata, "Metadata.pages.newPublication"],
    ["password reset", passwordResetMetadata, "Metadata.pages.passwordReset"],
    ["register", registerMetadata, "Metadata.pages.register"],
  ])("titles the %s page", async (_name, generate, expected) => {
    expect(await generate()).toEqual({ title: expected });
  });
});

describe("pages", () => {
  it("opens the chat of the room in the address", async () => {
    renderWithIntl(await ChatPage({ params: Promise.resolve({ id: "room-7" }) }));

    expect(screen.getByText("chat room-7")).toBeInTheDocument();
  });

  it("shows the chat list", () => {
    renderWithIntl(<ChatsPage />);

    expect(screen.getByText("lista de chats")).toBeInTheDocument();
  });

  it("shows the account", () => {
    renderWithIntl(<AccountPage />);

    expect(screen.getByText("cuenta")).toBeInTheDocument();
  });

  it("shows the creation of a publication", () => {
    renderWithIntl(<NewPublicationPage />);

    expect(screen.getByText("nueva publicación")).toBeInTheDocument();
  });

  it("frames the login form and links to registration", () => {
    renderWithIntl(<LoginPage />);

    expect(screen.getByRole("heading", { name: t("Auth.login.title") })).toBeInTheDocument();
    expect(screen.getByText("formulario de ingreso")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Auth.login.createAccount") })).toHaveAttribute("href", "/registro");
  });

  it("frames the registration form and links to login", () => {
    renderWithIntl(<RegisterPage />);

    expect(screen.getByRole("heading", { name: t("Auth.register.title") })).toBeInTheDocument();
    expect(screen.getByText("formulario de registro")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Auth.register.signIn") })).toHaveAttribute("href", "/ingresar");
  });

  it("frames the recovery form and links back to login", () => {
    renderWithIntl(<PasswordResetPage />);

    expect(screen.getByRole("heading", { name: t("Auth.passwordReset.title") })).toBeInTheDocument();
    expect(screen.getByText("formulario de recuperación")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Auth.passwordReset.backToLogin") })).toHaveAttribute("href", "/ingresar");
  });
});
