"use client";

import Link from "next/link";
import { accountMessages } from "@/lib/account/messages";
import { LOGIN_PATH, REGISTER_PATH } from "@/lib/auth/sign-in";
import { publicationMessages } from "@/lib/publications/messages";
import { NEW_PUBLICATION_PATH } from "@/lib/publications/paths";
import { Wordmark } from "@/components/brand/Wordmark";
import { SearchBox } from "@/components/publications/SearchBox";
import { UserBadge } from "./UserBadge";
import { useSessionState } from "./useSessionState";

const messages = accountMessages.header;

export function SiteHeader() {
  const session = useSessionState();

  return (
    <header className="site-header">
      <div className="site-header__brand">
        <Wordmark />
      </div>
      <SearchBox />
      <nav className="site-header__actions">
        {session.status === "member" && (
          <>
            <Link href={NEW_PUBLICATION_PATH} className="button button--primary site-header__cta">
              {publicationMessages.create.headerAction}
            </Link>
            <UserBadge name={session.name} />
          </>
        )}
        {session.status === "anonymous" && (
          <>
            <Link href={LOGIN_PATH} className="link">
              {messages.signIn}
            </Link>
            <Link href={REGISTER_PATH} className="button button--primary site-header__cta">
              {messages.signUp}
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}
