"use client";

import { useEffect, useState } from "react";
import { listMunicipalities } from "@/lib/api/municipalities";

export function useMunicipalityName(code: string): string {
  const [name, setName] = useState<string>();

  useEffect(() => {
    let active = true;
    listMunicipalities()
      .then((municipalities) => active && setName(municipalities.find((municipality) => municipality.code === code)?.name))
      .catch(() => undefined);
    return () => {
      active = false;
    };
  }, [code]);

  return name ?? code;
}
