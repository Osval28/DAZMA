# Verificación de fotografías de publicaciones

## Flujo

1. La API autoriza una carga privada con una URL firmada para `pending/{ownerId}/{photoId}`.
2. Una notificación de creación en R2 envía el objeto a la cola `dazma-photo-uploads`.
3. `photo-safety-worker` comprueba tamaño real, firma de archivo y tipo MIME. Envía los bytes a attachmentAV. Solo el resultado `clean` permite continuar.
4. Cloudflare Images decodifica y convierte la imagen a WebP con dimensiones máximas configuradas. El Worker guarda el resultado en `ready/{ownerId}/{photoId}` y elimina el original temporal.
5. Si la imagen es inválida o está infectada, el Worker deja un marcador en `rejected/` y elimina el original. Si el análisis o la conversión fallan, reintenta con espera creciente (30 s, 60 s...); al quinto intento la rechaza igual que una inválida. Nunca crea un objeto `ready/` sin un análisis `clean`.
6. El navegador consulta `GET /publications/api/v1/photos/{photoId}/status` con la sesión del propietario. Solo habilita la foto cuando recibe `READY`. La API únicamente copia desde `ready/` a `publications/` al crear la publicación.

El bucket sigue privado. La URL de carga solo permite escribir el objeto `pending/` autorizado; no otorga permisos para crear objetos `ready/` o `publications/`.

## Activación en Cloudflare

El Worker está en `photo-safety-worker/`. Se necesita una cuenta attachmentAV activa y una API key de su región. Elegir `ATTACHMENTAV_API_URL` para esa región; el valor inicial usa Estados Unidos. Configurar `MAX_IMAGE_BYTES` con el mismo límite que `dazma.publications.photo-upload.maximum-size` en Parameter Store. Mantenerlo por debajo de los 10 MB admitidos por el endpoint síncrono de attachmentAV.

Desde esa carpeta, con Wrangler autenticado:

```powershell
npm install
npx wrangler queues create dazma-photo-uploads
npx wrangler queues create dazma-photo-uploads-failed
npx wrangler secret put ATTACHMENTAV_API_KEY
npx wrangler deploy
npx wrangler r2 bucket notification create dazmaimages --event-type object-create --queue dazma-photo-uploads --prefix "pending/"
```

La regla de eventos se crea **después** de desplegar el Worker. Desde que la regla existe, el Worker mueve cada carga nueva de `pending/` a `ready/`; la API que hoy está en producción solo lee `pending/`, así que no se podrán crear publicaciones con fotos nuevas hasta desplegar la API nueva. Hacer la prueba y el despliegue de la API seguidos, en un horario sin usuarios. Revisar que no exista ya una regla superpuesta para el mismo prefijo. Probar una carga limpia y una prueba controlada de detección antes de desplegar la API y el frontend que exigen `ready/`.

Si la cola de fallos recibe mensajes o attachmentAV deja de responder, investigar y reprocesar. No se debe copiar manualmente un archivo de `pending/` a `ready/`. Configurar una política de retención para borrar `pending/` abandonados y marcadores `rejected/` antiguos. Los archivos ya publicados antes de este cambio no fueron analizados por este flujo; requieren una revisión separada.

## Vuelta atrás

Si algo falla después de activar la regla:

```powershell
npx wrangler r2 bucket notification delete dazmaimages --queue dazma-photo-uploads
```

y en Render, volver al despliegue anterior de la API desde el historial de despliegues. Las fotos que ya pasaron a `ready/` no las usa la API anterior; los usuarios deben cargarlas de nuevo.

## Comprobación

- `cd photo-safety-worker; npm test`: casos limpio, infectado, inconcluso, servicio caído, tipo falso, evento de otro bucket, reintento con espera y rechazo tras el último intento.
- Una foto limpia debe pasar de `PENDING` a `READY`; después se puede crear la publicación y el objeto final debe ser WebP en `publications/`.
- Una foto rechazada debe pasar a `REJECTED` y nunca debe poder asociarse a una publicación.
- Sin scanner o sin Worker, la API debe impedir la publicación de nuevas fotos. El navegador termina mostrando un fallo al cumplirse el tiempo de espera.

Referencias: [eventos R2](https://developers.cloudflare.com/r2/buckets/event-notifications/), [binding de Images](https://developers.cloudflare.com/images/optimization/binding/), [API de attachmentAV](https://attachmentav.com/help/virus-malware-scan-api/developer/definition.html).
