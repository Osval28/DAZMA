"use client";

import { useTranslations } from "next-intl";
import { useEffect, useState } from "react";
import { listMunicipalities, type Municipality } from "@/lib/api/municipalities";
import { SelectField } from "./SelectField";

type MunicipalityFieldProps = {
  id: string;
  name: string;
};

export function MunicipalityField({ id, name }: MunicipalityFieldProps) {
  const t = useTranslations("Auth.profile");
  const [municipalities, setMunicipalities] = useState<Municipality[]>([]);
  const [loading, setLoading] = useState(true);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    let active = true;
    listMunicipalities()
      .then((items) => active && setMunicipalities(items))
      .catch(() => active && setFailed(true))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, []);

  const placeholder = loading ? t("municipalitiesLoading") : failed ? t("municipalitiesFailed") : t("municipalityPlaceholder");

  return (
    <SelectField
      id={id}
      name={name}
      label={t("municipalityLabel")}
      placeholder={placeholder}
      options={municipalities.map((municipality) => ({ value: municipality.code, label: municipality.name }))}
      disabled={loading || failed}
    />
  );
}
