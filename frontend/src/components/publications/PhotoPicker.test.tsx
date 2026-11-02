import { fireEvent, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { PhotoPicker } from "./PhotoPicker";
import type { DraftPhoto, PhotoDrafts } from "./usePhotoDrafts";

function photo(key: string, upload: DraftPhoto["upload"]): DraftPhoto {
  return { key, file: new File(["x"], `${key}.png`, { type: "image/png" }), previewUrl: `https://preview.test/${key}.png`, upload };
}

function draftsWith(photos: DraftPhoto[], overrides: Partial<PhotoDrafts> = {}): PhotoDrafts {
  return {
    photos,
    uploadedIds: [],
    uploadingCount: 0,
    failedCount: 0,
    issue: undefined,
    add: vi.fn(),
    remove: vi.fn(),
    makeCover: vi.fn(),
    retry: vi.fn(),
    ...overrides,
  };
}

describe("PhotoPicker", () => {
  it("explains which photo is the cover and counts the photos", () => {
    renderWithIntl(<PhotoPicker drafts={draftsWith([photo("a", { status: "uploaded", photoId: "p1" })])} disabled={false} />);

    expect(screen.getByText(t("Publications.create.photos.hint"))).toBeInTheDocument();
    expect(screen.getByText("1")).toBeInTheDocument();
  });

  it("marks the first photo as the cover", () => {
    renderWithIntl(
      <PhotoPicker
        drafts={draftsWith([photo("a", { status: "uploaded", photoId: "p1" }), photo("b", { status: "uploaded", photoId: "p2" })])}
        disabled={false}
      />,
    );

    expect(screen.getAllByText(t("Publications.create.photos.cover"))).toHaveLength(1);
    expect(screen.getAllByRole("img")).toHaveLength(2);
  });

  it("shows the upload progress of each photo", () => {
    renderWithIntl(
      <PhotoPicker
        drafts={draftsWith([photo("a", { status: "uploading" }), photo("b", { status: "failed", reason: "Auth.errors.network" })])}
        disabled={false}
      />,
    );

    expect(screen.getByText(t("Publications.create.photos.uploading"))).toBeInTheDocument();
    expect(screen.getByText(t("Publications.create.photos.failed"))).toBeInTheDocument();
  });

  it("lets the user retry a failed photo", async () => {
    const drafts = draftsWith([photo("a", { status: "failed", reason: "Auth.errors.network" })]);
    renderWithIntl(<PhotoPicker drafts={drafts} disabled={false} />);

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.photos.retry") }));

    expect(drafts.retry).toHaveBeenCalledWith("a");
  });

  it("lets the user pick another cover only among the uploaded photos after the first", async () => {
    const drafts = draftsWith([
      photo("a", { status: "uploaded", photoId: "p1" }),
      photo("b", { status: "uploaded", photoId: "p2" }),
      photo("c", { status: "uploading" }),
    ]);
    renderWithIntl(<PhotoPicker drafts={drafts} disabled={false} />);

    const makeCover = screen.getAllByRole("button", { name: t("Publications.create.photos.makeCover") });
    expect(makeCover).toHaveLength(1);
    await userEvent.click(makeCover[0]);

    expect(drafts.makeCover).toHaveBeenCalledWith("b");
  });

  it("removes a photo", async () => {
    const drafts = draftsWith([photo("a", { status: "uploaded", photoId: "p1" })]);
    renderWithIntl(<PhotoPicker drafts={drafts} disabled={false} />);

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.photos.remove") }));

    expect(drafts.remove).toHaveBeenCalledWith("a");
  });

  it("hands the selected files to the drafts", () => {
    const drafts = draftsWith([]);
    const { container } = renderWithIntl(<PhotoPicker drafts={drafts} disabled={false} />);
    const file = new File(["x"], "nueva.png", { type: "image/png" });

    fireEvent.change(container.querySelector('input[type="file"]') as HTMLInputElement, { target: { files: [file] } });

    expect(drafts.add).toHaveBeenCalledWith([file]);
  });

  it("opens the file chooser from the add button", async () => {
    const { container } = renderWithIntl(<PhotoPicker drafts={draftsWith([])} disabled={false} />);
    const click = vi.spyOn(container.querySelector('input[type="file"]') as HTMLInputElement, "click");

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.photos.add") }));

    expect(click).toHaveBeenCalled();
  });

  it("leaves photo count validation to the backend", () => {
    const photos = Array.from({ length: 5 }, (_, index) => photo(`p${index}`, { status: "uploaded", photoId: `id${index}` }));
    renderWithIntl(<PhotoPicker drafts={draftsWith(photos)} disabled={false} />);

    expect(screen.getByRole("button", { name: t("Publications.create.photos.add") })).toBeEnabled();
  });

  it("disables every action while the form is submitting", () => {
    renderWithIntl(<PhotoPicker drafts={draftsWith([photo("a", { status: "failed", reason: "Auth.errors.network" })])} disabled />);

    for (const button of screen.getAllByRole("button")) {
      expect(button).toBeDisabled();
    }
  });

  it("announces the problem found with the last selection", () => {
    renderWithIntl(<PhotoPicker drafts={draftsWith([], { issue: "Publications.create.errors.photoTooLarge" })} disabled={false} />);

    expect(screen.getByRole("alert")).toHaveTextContent(t("Publications.create.errors.photoTooLarge"));
  });
});
