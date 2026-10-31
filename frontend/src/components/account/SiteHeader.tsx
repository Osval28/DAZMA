"use client";

import Link from "next/link";
import { accountMessages } from "@/lib/account/messages";
import { ACCOUNT_PATH, LOGIN_PATH, REGISTER_PATH } from "@/lib/auth/sign-in";
import { Wordmark } from "@/components/brand/Wordmark";
import { SearchBox } from "@/components/publications/SearchBox";
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
        {session === "member" && (
          <Link href={ACCOUNT_PATH} className="link">
            {messages.account}
          </Link>
        )}
        {session === "anonymous" && (
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
