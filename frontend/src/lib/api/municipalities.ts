import { apiJson } from "./http";

export type Municipality = {
  code: string;
  name: string;
  department: string;
};

export function listMunicipalities(): Promise<Municipality[]> {
  return apiJson<Municipality[]>("/shared/api/v1/municipalities");
}
