import type messages from "../../messages/es.json";

type AppMessages = typeof messages;

type AuthErrorName = keyof AppMessages["Auth"]["errors"] & string;

type PublicationErrorName = keyof AppMessages["Publications"]["create"]["errors"] & string;

type ChatErrorName = keyof AppMessages["Chats"]["errors"] & string;

export type AuthErrorKey = `Auth.errors.${AuthErrorName}`;

export type PublicationErrorKey = `Publications.create.errors.${PublicationErrorName}`;

export type ChatErrorKey = `Chats.errors.${ChatErrorName}`;

export type ErrorMessageKey = AuthErrorKey | PublicationErrorKey | ChatErrorKey;
