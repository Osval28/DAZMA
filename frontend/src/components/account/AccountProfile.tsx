"use client";

import { accountMessages } from "@/lib/account/messages";
import type { MyProfile } from "@/lib/api/users";
import { PinIcon } from "@/components/ui/icons";
import { AccountTabs, type AccountTab } from "./AccountTabs";
import { Avatar } from "./Avatar";
import { SignOutButton } from "./SignOutButton";
import { useMunicipalityName } from "./useMunicipalityName";

const messages = accountMessages.account;

type AccountProfileProps = {
  profile: MyProfile;
};

function ComingSoon({ text }: { text: string }) {
  return (
    <div className="coming-soon">
      <span className="badge badge--exchange">{messages.comingSoon}</span>
      <p>{text}</p>
    </div>
  );
}

export function AccountProfile({ profile }: AccountProfileProps) {
  const municipalityName = useMunicipalityName(profile.municipality);

  const tabs: AccountTab[] = [
    {
      id: "data",
      label: messages.tabs.data,
      content: (
        <>
          <dl className="profile-tiles">
            <div className="profile-tile">
              <dt>{messages.emailLabel}</dt>
              <dd>
                <span className="profile-tile__value">{profile.email}</span>
                <span className={`badge ${profile.emailVerified ? "badge--purchase" : "badge--exchange"}`}>
                  {profile.emailVerified ? messages.verifiedBadge : messages.unverifiedBadge}
                </span>
              </dd>
            </div>
            <div className="profile-tile">
              <dt>{messages.phoneLabel}</dt>
              <dd>
                <span className="profile-tile__value">{profile.phone}</span>
              </dd>
            </div>
          </dl>
          {!profile.emailVerified && <p className="profile__notice">{messages.emailNotVerified}</p>}
        </>
      ),
    },
    { id: "publications", label: messages.tabs.publications, content: <ComingSoon text={messages.publicationsSoon} /> },
    { id: "security", label: messages.tabs.security, content: <ComingSoon text={messages.securitySoon} /> },
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
      <AccountTabs label={messages.tabsLabel} tabs={tabs} />
      <SignOutButton />
    </section>
  );
}
