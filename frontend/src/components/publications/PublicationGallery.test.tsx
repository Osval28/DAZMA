import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { renderWithIntl, t } from "@/test/intl";
import { PublicationGallery } from "./PublicationGallery";

const photos = [
  { id: "1", url: "https://images.test/1.jpg", position: 0 },
  { id: "2", url: "https://images.test/2.jpg", position: 1 },
  { id: "3", url: "https://images.test/3.jpg", position: 2 },
];

describe("PublicationGallery", () => {
  it("shows the first photo with the title as description", () => {
    renderWithIntl(<PublicationGallery photos={photos} title="iPhone 12" />);

    expect(screen.getByRole("group", { name: t("Publications.detail.galleryLabel") })).toBeInTheDocument();
    expect(screen.getByRole("img", { name: "iPhone 12" })).toHaveAttribute("src", "https://images.test/1.jpg");
  });

  it("offers thumbnails when there are several photos", () => {
    renderWithIntl(<PublicationGallery photos={photos} title="iPhone 12" />);

    expect(screen.getAllByRole("button")).toHaveLength(3);
    expect(screen.getByRole("button", { name: t("Publications.detail.thumbnailLabel", { position: 1 }) })).toHaveAttribute("aria-pressed", "true");
  });

  it("does not offer thumbnails for a single photo", () => {
    renderWithIntl(<PublicationGallery photos={[photos[0]]} title="iPhone 12" />);

    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });

  it("shows the photo chosen in the thumbnails", async () => {
    renderWithIntl(<PublicationGallery photos={photos} title="iPhone 12" />);

    await userEvent.click(screen.getByRole("button", { name: t("Publications.detail.thumbnailLabel", { position: 3 }) }));

    expect(screen.getByRole("img", { name: "iPhone 12" })).toHaveAttribute("src", "https://images.test/3.jpg");
    expect(screen.getByRole("button", { name: t("Publications.detail.thumbnailLabel", { position: 3 }) })).toHaveAttribute("aria-pressed", "true");
  });
});
