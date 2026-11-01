"use client";

import { useTranslations } from "next-intl";
import type { MyProfile } from "@/lib/api/users";
import { ACCOUNT_PATH } from "@/lib/auth/sign-in";
import { PinIcon } from "@/components/ui/icons";
import { AccountTabs, type AccountTab } from "./AccountTabs";
import { Avatar } from "./Avatar";
import { SignOutButton } from "./SignOutButton";
import { VerifyEmailButton } from "./VerifyEmailButton";
import { useMunicipalityName } from "./useMunicipalityName";

type AccountProfileProps = {
  profile: MyProfile;
  onReload: () => Promise<void>;
};

function ComingSoon({ text }: { text: string }) {
  const t = useTranslations("Account.account");

  return (
    <div className="coming-soon">
      <span className="badge badge--exchange">{t("comingSoon")}</span>
      <p>{text}</p>
    </div>
  );
}

export function AccountProfile({ profile, onReload }: AccountProfileProps) {
  const t = useTranslations("Account.account");
  const municipalityName = useMunicipalityName(profile.municipality);

  const tabs: AccountTab[] = [
    {
      id: "data",
      label: t("tabs.data"),
      content: (
        <>
          <dl className="profile-tiles">
            <div className="profile-tile">
              <dt>{t("emailLabel")}</dt>
              <dd>
                <span className="profile-tile__value">{profile.email}</span>
                <span className={`badge ${profile.emailVerified ? "badge--purchase" : "badge--exchange"}`}>
                  {profile.emailVerified ? t("verifiedBadge") : t("unverifiedBadge")}
                </span>
              </dd>
            </div>
            <div className="profile-tile">
              <dt>{t("phoneLabel")}</dt>
              <dd>
                <span className="profile-tile__value">{profile.phone}</span>
              </dd>
            </div>
          </dl>
          {!profile.emailVerified && (
            <>
              <p className="profile__notice">{t("emailNotVerified")}</p>
              <VerifyEmailButton returnPath={ACCOUNT_PATH} onRefreshed={onReload} />
            </>
          )}
        </>
      ),
    },
    { id: "publications", label: t("tabs.publications"), content: <ComingSoon text={t("publicationsSoon")} /> },
    { id: "security", label: t("tabs.security"), content: <ComingSoon text={t("securitySoon")} /> },
  ];

  return (
    <section className="account__card">
      <div className="account__identity">
        <Avatar name={profile.name} size="large" />
        <div>
          <h1 className="account__name">{profile.name}</h1>
          <p className="account__location">
            <PinIcon />
            {municipalityName}
          </p>
        </div>
      </div>
      <AccountTabs label={t("tabsLabel")} tabs={tabs} />
      <SignOutButton />
    </section>
  );
}
