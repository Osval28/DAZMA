import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import { ChatView } from "@/components/chats/ChatView";

type ChatPageProps = {
  params: Promise<{ id: string }>;
};

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Metadata.pages");
  return { title: t("chat") };
}

export default async function ChatPage({ params }: ChatPageProps) {
  const { id } = await params;
  return <ChatView roomId={id} />;
}
