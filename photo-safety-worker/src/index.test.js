import assert from "node:assert/strict";
import { afterEach, test } from "node:test";
import { handleMessage, processUpload } from "./index.js";

const key = "pending/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222";
const jpeg = Uint8Array.from([255, 216, 255, 0, 0]);
const event = { action: "PutObject", bucket: "dazmaimages", object: { key, eTag: "etag" } };
const originalFetch = globalThis.fetch;
afterEach(() => { globalThis.fetch = originalFetch; });

function fixtures(bytes = jpeg, contentType = "image/jpeg") {
  const writes = [];
  const deletes = [];
  const env = {
    BUCKET_NAME: "dazmaimages",
    MAX_IMAGE_BYTES: 5242880,
    MAX_IMAGE_DIMENSION: 1600,
    ATTACHMENTAV_API_URL: "https://scanner.example",
    ATTACHMENTAV_API_KEY: "secret",
    PHOTOS: {
      get: async () => ({ etag: "etag", size: bytes.length, httpMetadata: { contentType }, arrayBuffer: async () => bytes.buffer }),
      put: async (...args) => { writes.push(args); },
      delete: async (objectKey) => { deletes.push(objectKey); },
    },
    IMAGES: { input: () => ({ transform: () => ({ output: () => ({ response: () => new Response("optimized") }) }) }) },
  };
  return { env, writes, deletes };
}

test("a clean image is re-encoded before becoming ready", async () => {
  const { env, writes, deletes } = fixtures();
  globalThis.fetch = async () => Response.json({ status: "clean" });
  await processUpload(event, env);
  assert.equal(writes.length, 1);
  assert.equal(writes[0][0], key.replace("pending/", "ready/"));
  assert.equal(writes[0][2].httpMetadata.contentType, "image/webp");
  assert.deepEqual(deletes, [key]);
});

test("infected images are deleted and never approved", async () => {
  const { env, writes, deletes } = fixtures();
  globalThis.fetch = async () => Response.json({ status: "infected" });
  await processUpload(event, env);
  assert.equal(writes.length, 1);
  assert.equal(writes[0][0], key.replace("pending/", "rejected/"));
  assert.deepEqual(deletes, [key]);
});

test("an unavailable scanner never approves an image", async () => {
  const { env, writes } = fixtures();
  globalThis.fetch = async () => new Response("error", { status: 503 });
  await assert.rejects(processUpload(event, env));
  assert.equal(writes.length, 0);
});

test("an inconclusive scan never approves an image", async () => {
  const { env, writes, deletes } = fixtures();
  globalThis.fetch = async () => Response.json({ status: "no" });
  await assert.rejects(processUpload(event, env));
  assert.equal(writes.length, 0);
  assert.equal(deletes.length, 0);
});

test("events for another bucket never touch DAZMA photos", async () => {
  const { env, writes, deletes } = fixtures();
  await processUpload({ ...event, bucket: "other" }, env);
  assert.equal(writes.length, 0);
  assert.equal(deletes.length, 0);
});

test("a forged JPEG content type never reaches the scanner", async () => {
  const { env, writes, deletes } = fixtures(Uint8Array.from([1, 2, 3]), "image/jpeg");
  globalThis.fetch = async () => { throw new Error("Scanner must not be called"); };
  await processUpload(event, env);
  assert.equal(writes.length, 1);
  assert.equal(writes[0][0], key.replace("pending/", "rejected/"));
  assert.deepEqual(deletes, [key]);
});

function queueMessage(attempts) {
  const calls = { acked: 0, retried: [] };
  return {
    calls,
    message: {
      body: event,
      attempts,
      ack: () => { calls.acked += 1; },
      retry: (options) => { calls.retried.push(options); },
    },
  };
}

test("a temporary failure is retried with backoff and nothing is approved", async () => {
  const { env, writes, deletes } = fixtures();
  globalThis.fetch = async () => new Response("error", { status: 503 });
  const { message, calls } = queueMessage(1);
  await handleMessage(message, env);
  assert.equal(calls.acked, 0);
  assert.deepEqual(calls.retried, [{ delaySeconds: 30 }]);
  assert.equal(writes.length, 0);
  assert.equal(deletes.length, 0);
});

test("after the last attempt the photo is rejected instead of staying pending", async () => {
  const { env, writes, deletes } = fixtures();
  globalThis.fetch = async () => new Response("error", { status: 503 });
  const { message, calls } = queueMessage(5);
  await handleMessage(message, env);
  assert.equal(calls.acked, 1);
  assert.equal(calls.retried.length, 0);
  assert.equal(writes.length, 1);
  assert.equal(writes[0][0], key.replace("pending/", "rejected/"));
  assert.deepEqual(deletes, [key]);
});

test("an image that cannot be converted is never approved", async () => {
  const { env, writes } = fixtures();
  env.IMAGES = { input: () => ({ transform: () => ({ output: async () => { throw new Error("9422"); } }) }) };
  globalThis.fetch = async () => Response.json({ status: "clean" });
  const { message, calls } = queueMessage(5);
  await handleMessage(message, env);
  assert.equal(calls.acked, 1);
  assert.equal(writes.length, 1);
  assert.equal(writes[0][0], key.replace("pending/", "rejected/"));
});
