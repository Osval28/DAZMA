import type { ReactNode } from "react";
import { Wordmark } from "@/components/brand/Wordmark";

type AuthShellProps = {
  title: string;
  subtitle: string;
  children: ReactNode;
  footer: ReactNode;
};

export function AuthShell({ title, subtitle, children, footer }: AuthShellProps) {
  return (
    <main className="auth-shell">
      <div className="auth-shell__card">
        <Wordmark />
        <h1 className="auth-shell__title">{title}</h1>
        <p className="auth-shell__subtitle">{subtitle}</p>
        {children}
        <p className="auth-shell__footer">{footer}</p>
      </div>
    </main>
  );
}
