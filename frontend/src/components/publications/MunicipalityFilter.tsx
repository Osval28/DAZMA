"use client";

import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import type { ChangeEvent } from "react";
import type { Municipality } from "@/lib/api/municipalities";
import { listingHref } from "@/lib/publications/listing-query";

type MunicipalityFilterProps = {
  municipalities: Municipality[];
  selected?: string;
};

export function MunicipalityFilter({ municipalities, selected }: MunicipalityFilterProps) {
  const t = useTranslations("Publications.filter");
  const router = useRouter();

  function handleChange(event: ChangeEvent<HTMLSelectElement>) {
    const municipality = event.target.value === "" ? undefined : event.target.value;
    router.push(listingHref({ municipality, page: 0 }));
  }

  return (
    <div className="field listing__filter">
      <label htmlFor="listing-municipality" className="field__label">
        {t("municipalityLabel")}
      </label>
      <select key={selected ?? "all"} id="listing-municipality" className="field__input" defaultValue={selected ?? ""} onChange={handleChange}>
        <option value="">{t("allMunicipalities")}</option>
        {municipalities.map((municipality) => (
          <option key={municipality.code} value={municipality.code}>
            {municipality.name}
          </option>
        ))}
      </select>
    </div>
  );
}
