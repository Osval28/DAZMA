import { publicationMessages } from "@/lib/publications/messages";

const messages = publicationMessages.search;

export function SearchBox() {
  return (
    <div className="search">
      <label htmlFor="listing-search" className="visually-hidden">
        {messages.label}
      </label>
      <input id="listing-search" className="field__input" type="search" placeholder={messages.placeholder} disabled />
    </div>
  );
}
