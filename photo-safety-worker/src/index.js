const MAX_ATTEMPTS = 5;
const PENDING_KEY = /^pending\/[0-9a-f-]{36}\/[0-9a-f-]{36}$/;

function detectedType(bytes) {
  if (bytes.length >= 3 && bytes[0] === 255 && bytes[1] === 216 && bytes[2] === 255) return "image/jpeg";
  if (bytes.length >= 8 && [137, 80, 78, 71, 13, 10, 26, 10].every((n, i) => bytes[i] === n)) return "image/png";
  if (bytes.length >= 12 && new TextDecoder().decode(bytes.slice(0, 4)) === "RIFF"
      && new TextDecoder().decode(bytes.slice(8, 12)) === "WEBP") return "image/webp";
  return null;
}

async function reject(env, key) {
  await env.PHOTOS.put(key.replace(/^pending\//, "rejected/"), "rejected", {
    httpMetadata: { contentType: "text/plain" },
  });
  await env.PHOTOS.delete(key);
}

export async function processUpload(event, env) {
  if (event.action !== "PutObject") return;
  if (event.bucket !== env.BUCKET_NAME) return;
  const key = event.object?.key;
  if (!PENDING_KEY.test(key ?? "")) return;
  const object = await env.PHOTOS.get(key);
  if (!object || object.etag !== event.object.eTag) return;
  if (object.size < 1 || object.size > Number(env.MAX_IMAGE_BYTES)) {
    await reject(env, key);
    return;
  }
  const bytes = new Uint8Array(await object.arrayBuffer());
  const type = detectedType(bytes);
  if (!type || type !== object.httpMetadata?.contentType) {
    await reject(env, key);
    return;
  }
  if (!env.ATTACHMENTAV_API_KEY) throw new Error("Antivirus API key is missing");
  const response = await fetch(`${env.ATTACHMENTAV_API_URL}/v1/scan/sync/binary`, {
    method: "POST",
    headers: { "x-api-key": env.ATTACHMENTAV_API_KEY, "Content-Type": "application/octet-stream" },
    body: bytes,
  });
  if (!response.ok) throw new Error(`Antivirus unavailable: ${response.status}`);
  const result = await response.json();
  if (result.status === "infected") {
    await reject(env, key);
    console.warn(JSON.stringify({ event: "photo.scan.infected", key }));
    return;
  }
  if (result.status !== "clean") throw new Error("Antivirus returned no conclusive result");
  const optimized = (await env.IMAGES.input(bytes)
    .transform({ width: Number(env.MAX_IMAGE_DIMENSION), height: Number(env.MAX_IMAGE_DIMENSION), fit: "scale-down" })
    .output({ format: "image/webp", quality: 80, anim: false })).response();
  if (!optimized.ok) throw new Error(`Image conversion failed: ${optimized.status}`);
  const readyKey = key.replace(/^pending\//, "ready/");
  await env.PHOTOS.put(readyKey, optimized.body, { httpMetadata: { contentType: "image/webp" } });
  await env.PHOTOS.delete(key);
  console.info(JSON.stringify({ event: "photo.scan.ready", key: readyKey }));
}

// A failure is retried with backoff. After the last attempt the photo is rejected instead of staying
// pending forever: it is never approved, and the user uploads another one.
export async function handleMessage(message, env) {
  try {
    await processUpload(message.body, env);
    message.ack();
    return;
  } catch (error) {
    const key = message.body?.object?.key;
    console.error(JSON.stringify({
      event: "photo.scan.failed", key, attempt: message.attempts, error: String(error?.message ?? error),
    }));
    const lastAttempt = message.attempts >= MAX_ATTEMPTS;
    if (lastAttempt && message.body?.bucket === env.BUCKET_NAME && PENDING_KEY.test(key ?? "")) {
      try {
        await reject(env, key);
        console.warn(JSON.stringify({ event: "photo.scan.gave_up", key }));
        message.ack();
        return;
      } catch (rejectError) {
        console.error(JSON.stringify({ event: "photo.scan.reject_failed", key, error: String(rejectError) }));
      }
    }
    message.retry({ delaySeconds: Math.min(30 * message.attempts, 300) });
  }
}

export default {
  async queue(batch, env) {
    for (const message of batch.messages) {
      await handleMessage(message, env);
    }
  },
};
