import { useTranslations } from "next-intl";
import { SearchIcon } from "@/components/ui/icons";

export function SearchBox() {
  const t = useTranslations("Publications.search");

  return (
    <div className="search">
      <label htmlFor="listing-search" className="visually-hidden">
        {t("label")}
      </label>
      <span className="search__icon">
        <SearchIcon />
      </span>
      <input id="listing-search" className="field__input search__input" type="search" placeholder={t("placeholder")} disabled />
    </div>
  );
}
