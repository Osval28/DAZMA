"use client";

import { useEffect, useState } from "react";
import { listMunicipalities, type Municipality } from "@/lib/api/municipalities";
import { authMessages } from "@/lib/auth/messages";
import { SelectField } from "./SelectField";

const messages = authMessages.profile;

type MunicipalityFieldProps = {
  id: string;
  name: string;
};

export function MunicipalityField({ id, name }: MunicipalityFieldProps) {
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

  const placeholder = loading ? messages.municipalitiesLoading : failed ? messages.municipalitiesFailed : messages.municipalityPlaceholder;

  return (
    <SelectField
      id={id}
      name={name}
      label={messages.municipalityLabel}
      placeholder={placeholder}
      options={municipalities.map((municipality) => ({ value: municipality.code, label: municipality.name }))}
      disabled={loading || failed}
    />
  );
}
