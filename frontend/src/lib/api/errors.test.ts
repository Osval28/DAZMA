import { describe, expect, it } from "vitest";
import { ApiError, CsrfUnavailableError, NetworkError, PhotoUploadError, hasErrorCode, isUnauthenticated } from "./errors";

describe("API errors", () => {
  it("keeps the status and code of an ApiError", () => {
    const error = new ApiError(409, "CHAT_PARTICIPANT_NOT_READY");

    expect(error.status).toBe(409);
    expect(error.code).toBe("CHAT_PARTICIPANT_NOT_READY");
    expect(error.name).toBe("ApiError");
    expect(error.message).toBe("CHAT_PARTICIPANT_NOT_READY");
  });

  it("names the technical errors", () => {
    expect(new NetworkError().name).toBe("NetworkError");
    expect(new CsrfUnavailableError().name).toBe("CsrfUnavailableError");
    expect(new PhotoUploadError().name).toBe("PhotoUploadError");
  });

  it("detects an unauthenticated response only for ApiError 401", () => {
    expect(isUnauthenticated(new ApiError(401, "AUTHENTICATION_REQUIRED"))).toBe(true);
    expect(isUnauthenticated(new ApiError(403, "ACCESS_DENIED"))).toBe(false);
    expect(isUnauthenticated(new Error("401"))).toBe(false);
  });

  it("matches an error code only on ApiError", () => {
    expect(hasErrorCode(new ApiError(404, "USER_NOT_FOUND"), "USER_NOT_FOUND")).toBe(true);
    expect(hasErrorCode(new ApiError(404, "OTHER"), "USER_NOT_FOUND")).toBe(false);
    expect(hasErrorCode(new Error("USER_NOT_FOUND"), "USER_NOT_FOUND")).toBe(false);
  });
});
