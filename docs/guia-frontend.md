# Guía rápida para el frontend

Contrato completo en `docs/openapi/*.yaml`. Esta guía resume el orden de las llamadas y las trampas.

## Reglas generales

- Todas las llamadas con `credentials: 'include'` (la sesión viaja en la cookie `dazma_session`, HttpOnly).
- Toda llamada que no sea GET lleva el encabezado `X-XSRF-TOKEN` con el valor de la cookie `XSRF-TOKEN`
  (primero `GET /security/api/v1/sessions/csrf`).
- Los errores llegan como `application/problem+json` con `status`, `title`, `detail` y `code`. Decide por `code`, no por `detail`.
- El municipio siempre viaja como **código DIVIPOLA de 5 dígitos**; los nombres salen de `GET /shared/api/v1/municipalities`.
- Rutas públicas (sin sesión): municipios, listado y detalle de publicaciones, csrf.

## Flujo de una cuenta nueva

1. Firebase Authentication en el navegador (registro/inicio de sesión) → ID token.
2. `GET /security/api/v1/sessions/csrf` → cookie `XSRF-TOKEN`.
3. `POST /security/api/v1/sessions` con `{ idToken }` → cookie `dazma_session`. El ID token debe tener menos de
   5 minutos desde el inicio de sesión (`RECENT_SIGN_IN_REQUIRED` si no). Descarta el ID token después.
4. `GET /shared/api/v1/municipalities` → llena el selector (puede cachearse un día).
5. `POST /users/api/v1/users` con nombre, celular y municipio → perfil creado (201).
6. `GET /users/api/v1/users/me` para pintar el perfil y saber `emailVerified`.

## Publicar

1. Verificar que `emailVerified` sea `true` (si no, mostrar el aviso; ver más abajo).
2. Por cada foto: `POST /publications/api/v1/photos/upload-authorizations` → `{ photoId, uploadUrl, expiresAt }`.
3. `PUT uploadUrl` con el archivo y **el mismo `Content-Type`** que declaraste (directo al almacenamiento, sin cookie ni CSRF).
4. `POST /publications/api/v1/publications` con `photoUploadIds` en el orden deseado (la primera es la portada).
5. 201 con `Location` hacia el detalle.

Errores que debes manejar: `PUBLICATION_OWNER_EMAIL_NOT_VERIFIED` (403), `MUNICIPALITY_NOT_FOUND` (400),
`PUBLICATION_OWNER_NOT_READY` (409: reintentar en unos segundos), `PUBLICATION_PHOTO_UPLOAD_NOT_AVAILABLE` (409:
la foto no se subió o expiró).

## Pantalla principal

`GET /publications/api/v1/publications?municipality=05615&page=0&size=20`

- `items[].coverUrl` es una URL firmada que vence; no la guardes.
- No hay total: sigue pidiendo `page + 1` mientras `hasNext` sea `true`.
- Sin `municipality` se listan todos.

## `emailVerified` y la cookie de sesión

La cookie se crea a partir del ID token y **congela** `email_verified` en ese momento. Si el usuario verifica su
correo después, el backend sigue viendo `false` hasta que se abra una sesión nueva. Como el backend exige un
inicio de sesión de menos de 5 minutos, el flujo recomendado es:

1. Al recibir `PUBLICATION_OWNER_EMAIL_NOT_VERIFIED`, mostrar "Verifica tu correo".
2. Cuando el usuario diga que ya lo verificó: `await user.reload()` y comprobar `user.emailVerified`.
3. Reautenticar (`reauthenticateWithCredential`) para refrescar `auth_time`, luego `await user.getIdToken(true)`.
4. `POST /security/api/v1/sessions` con ese token → cookie nueva con `email_verified = true`.

Si la reautenticación no es posible, cerrar sesión (`DELETE /security/api/v1/sessions`) y pedir iniciar sesión de nuevo.

## Imágenes con Next.js

Las URLs firmadas cambian en cada respuesta: usa `images.unoptimized: true` o `<img>` y agrega el host del
almacenamiento en `images.remotePatterns`.
