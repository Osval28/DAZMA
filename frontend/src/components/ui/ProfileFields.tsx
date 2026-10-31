import { authMessages } from "@/lib/auth/messages";
import { MunicipalityField } from "./MunicipalityField";
import { TextField } from "./TextField";

const messages = authMessages.profile;

export function ProfileFields({ idPrefix }: { idPrefix: string }) {
  return (
    <>
      <TextField id={`${idPrefix}-name`} name="name" label={messages.nameLabel} placeholder={messages.namePlaceholder} autoComplete="name" />
      <TextField id={`${idPrefix}-phone`} name="phone" type="tel" inputMode="numeric" label={messages.phoneLabel} placeholder={messages.phonePlaceholder} autoComplete="tel" />
      <MunicipalityField id={`${idPrefix}-municipality`} name="municipality" />
    </>
  );
}
