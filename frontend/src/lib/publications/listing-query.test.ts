import { describe, expect, it } from "vitest";
import { listingHref, parseListingQuery } from "./listing-query";

describe("parseListingQuery", () => {
  it("uses the first page and no filter by default", () => {
    expect(parseListingQuery({})).toEqual({ municipality: undefined, page: 0 });
  });

  it("reads a valid municipality and page", () => {
    expect(parseListingQuery({ municipality: "05615", page: "3" })).toEqual({ municipality: "05615", page: 3 });
  });

  it("uses the first value of repeated parameters", () => {
    expect(parseListingQuery({ municipality: ["05615", "05001"], page: ["2", "9"] })).toEqual({
      municipality: "05615",
      page: 2,
    });
  });

  it.each(["5615", "056150", "abcde", ""])("ignores the invalid municipality %j", (municipality) => {
    expect(parseListingQuery({ municipality }).municipality).toBeUndefined();
  });

  it.each(["-1", "abc", "1.5", "1234567", ""])("ignores the invalid page %j", (page) => {
    expect(parseListingQuery({ page }).page).toBe(0);
  });

});

describe("listingHref", () => {
  it("points home when there is nothing to filter", () => {
    expect(listingHref({ page: 0 })).toBe("/");
  });

  it("includes the municipality", () => {
    expect(listingHref({ municipality: "05615", page: 0 })).toBe("/?municipality=05615");
  });

  it("includes the page only after the first one", () => {
    expect(listingHref({ page: 2 })).toBe("/?page=2");
  });

  it("combines municipality and page", () => {
    expect(listingHref({ municipality: "05615", page: 2 })).toBe("/?municipality=05615&page=2");
  });
});
