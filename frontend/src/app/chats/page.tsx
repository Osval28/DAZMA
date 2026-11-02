import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import { ChatListView } from "@/components/chats/ChatListView";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Metadata.pages");
  return { title: t("chats") };
}

export default function ChatsPage() {
  return <ChatListView />;
}
