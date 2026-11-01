"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import type { FormEvent } from "react";
import { createPublication, type PublicationType } from "@/lib/api/publications";
import { MAXIMUM_DESCRIPTION_LENGTH, MAXIMUM_NAME_LENGTH, parsePrice, validateDraft } from "@/lib/publications/draft-validation";
import { describeDraftIssue, describePublicationError } from "@/lib/publications/errors";
import { publicationMessages } from "@/lib/publications/messages";
import { publicationPath } from "@/lib/publications/paths";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { MunicipalityField } from "@/components/ui/MunicipalityField";
import { TextAreaField } from "@/components/ui/TextAreaField";
import { TextField } from "@/components/ui/TextField";
import { PhotoPicker } from "./PhotoPicker";
import { usePhotoDrafts } from "./usePhotoDrafts";

const messages = publicationMessages.create;
const typeOptions: PublicationType[] = ["PURCHASE", "EXCHANGE"];

export function PublicationForm() {
  const router = useRouter();
  const drafts = usePhotoDrafts();
  const [type, setType] = useState<PublicationType>("PURCHASE");
  const [errorMessage, setErrorMessage] = useState<string>();
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const name = String(form.get("name")).trim();
    const description = String(form.get("description")).trim();
    const price = String(form.get("price")).trim();
    const municipality = String(form.get("municipality") ?? "");

    const issue = validateDraft(
      { name, description, price, municipality },
      { uploaded: drafts.uploadedIds.length, uploading: drafts.uploadingCount, failed: drafts.failedCount },
    );
    if (issue !== undefined) {
      setErrorMessage(describeDraftIssue(issue));
      return;
    }

    setErrorMessage(undefined);
    setSubmitting(true);
    try {
      const created = await createPublication({
        name,
        type,
        description,
        municipality,
        price: parsePrice(price) as number,
        photoUploadIds: drafts.uploadedIds,
      });
      router.replace(publicationPath(created.id));
    } catch (error) {
      setErrorMessage(describePublicationError(error));
      setSubmitting(false);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      {errorMessage !== undefined && <Alert>{errorMessage}</Alert>}
      <section className="form-section">
        <h2 className="form-section__title">{messages.sections.photos}</h2>
        <PhotoPicker drafts={drafts} disabled={submitting} />
      </section>
      <section className="form-section">
        <h2 className="form-section__title">{messages.sections.offer}</h2>
        <fieldset className="choice" disabled={submitting}>
          <legend className="field__label">{messages.typeLabel}</legend>
          <div className="choice__options">
            {typeOptions.map((option) => (
              <label key={option} className="choice__option">
                <input type="radio" name="type" value={option} checked={type === option} onChange={() => setType(option)} />
                <span>{messages.typeOptions[option]}</span>
              </label>
            ))}
          </div>
        </fieldset>
        <TextField
          id="publication-name"
          name="name"
          label={messages.nameLabel}
          placeholder={messages.namePlaceholder}
          maxLength={MAXIMUM_NAME_LENGTH}
          disabled={submitting}
        />
        <TextAreaField
          id="publication-description"
          name="description"
          label={messages.descriptionLabel}
          placeholder={messages.descriptionPlaceholder}
          maxLength={MAXIMUM_DESCRIPTION_LENGTH}
          rows={5}
          disabled={submitting}
        />
      </section>
      <section className="form-section">
        <h2 className="form-section__title">{messages.sections.priceAndPlace}</h2>
        <div className="form__row">
          <TextField
            id="publication-price"
            name="price"
            label={type === "EXCHANGE" ? messages.referenceValueLabel : messages.priceLabel}
            placeholder={messages.pricePlaceholder}
            inputMode="numeric"
            autoComplete="off"
            disabled={submitting}
          />
          <MunicipalityField id="publication-municipality" name="municipality" />
        </div>
        <p className="form__hint">{messages.priceHint}</p>
      </section>
      <Button type="submit" disabled={submitting}>
        {submitting ? messages.submitting : messages.submit}
      </Button>
    </form>
  );
}
