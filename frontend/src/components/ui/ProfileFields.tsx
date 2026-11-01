import { useTranslations } from "next-intl";
import { MunicipalityField } from "./MunicipalityField";
import { TextField } from "./TextField";

export function ProfileFields({ idPrefix }: { idPrefix: string }) {
  const t = useTranslations("Auth.profile");

  return (
    <>
      <TextField id={`${idPrefix}-name`} name="name" label={t("nameLabel")} placeholder={t("namePlaceholder")} autoComplete="name" />
      <TextField id={`${idPrefix}-phone`} name="phone" type="tel" inputMode="numeric" label={t("phoneLabel")} placeholder={t("phonePlaceholder")} autoComplete="tel" />
      <MunicipalityField id={`${idPrefix}-municipality`} name="municipality" />
    </>
  );
}
