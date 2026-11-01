import { SearchIcon } from "@/components/ui/icons";
import { publicationMessages } from "@/lib/publications/messages";

const messages = publicationMessages.search;

export function SearchBox() {
  return (
    <div className="search">
      <label htmlFor="listing-search" className="visually-hidden">
        {messages.label}
      </label>
      <span className="search__icon">
        <SearchIcon />
      </span>
      <input id="listing-search" className="field__input search__input" type="search" placeholder={messages.placeholder} disabled />
    </div>
  );
}
