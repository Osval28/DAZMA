import type { Metadata } from "next";
import { Bricolage_Grotesque, Space_Grotesk } from "next/font/google";
import { NextIntlClientProvider } from "next-intl";
import { getLocale, getTranslations } from "next-intl/server";
import "./globals.css";

const spaceGrotesk = Space_Grotesk({
  subsets: ["latin"],
  variable: "--font-space-grotesk",
  display: "swap",
});

const bricolageGrotesque = Bricolage_Grotesque({
  subsets: ["latin"],
  weight: "700",
  variable: "--font-bricolage-grotesque",
  display: "swap",
});

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Metadata");
  return {
    title: t("title"),
    description: t("description"),
  };
}

export default async function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  const locale = await getLocale();
  const t = await getTranslations("Account");

  return (
    <html lang={locale} className={`${spaceGrotesk.variable} ${bricolageGrotesque.variable}`}>
      <body>
        <NextIntlClientProvider>
          <a href="#contenido" className="skip-link">
            {t("skipToContent")}
          </a>
          {children}
        </NextIntlClientProvider>
      </body>
    </html>
  );
}
