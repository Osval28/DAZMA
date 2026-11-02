# Entrega de configuración: Cloudflare R2 y Render

Esta guía comienza cuando el código de publicaciones ya está integrado en la rama que despliega el backend. No contiene credenciales reales y ningún secreto debe agregarse al repositorio.

## 1. Preparar Cloudflare R2

1. Crear un bucket privado para las fotografías de publicaciones.
2. Crear una credencial de la API S3 de R2 con permiso de lectura y escritura limitado únicamente a ese bucket.
3. Guardar una sola vez el `Access Key ID` y el `Secret Access Key`.
4. Copiar el endpoint S3 de la cuenta, con el formato `https://<ACCOUNT_ID>.r2.cloudflarestorage.com`.
5. Configurar CORS en el bucket para permitir solicitudes `PUT` desde el dominio real del frontend. La configuración debe admitir el encabezado `Content-Type` y puede exponer `ETag`. El dominio de producción debe escribirse de forma explícita; no se debe usar `*` como origen de producción.

Cloudflare documenta la creación del bucket y las credenciales en [S3 API](https://developers.cloudflare.com/r2/get-started/s3/) y el uso de URL firmadas y CORS en [Presigned URLs](https://developers.cloudflare.com/r2/api/s3/presigned-urls/).

## 2. Configuración del backend en producción

La aplicación lee los secretos de `dazma/prod/r2` en AWS Secrets Manager y los parámetros desde `/dazma/prod/` en AWS Parameter Store. Configurar las siguientes claves:

| Variable | Valor esperado | Secreta |
| --- | --- | --- |
| `r2.accessKeyId` | Access Key ID en `dazma/prod/r2` | Sí |
| `r2.secretAccessKey` | Secret Access Key en `dazma/prod/r2` | Sí |
| `dazma.publications.photo-upload.allowed-content-types` | `image/jpeg,image/png,image/webp` | No |
| `dazma.publications.photo-upload.maximum-size` | `5MB` | No |
| `dazma.publications.photo-upload.validity` | `5m` | No |
| `dazma.publications.photo-download.validity` | `10m` | No |

Los límites de 5 MB y 5 minutos son la configuración usada para validar el flujo actual. Si el producto cambia estos límites, deben modificarse de forma consciente y probarse de nuevo.

El endpoint y el bucket R2 están definidos en `application-prod.properties`. Render debe tener acceso a los almacenes AWS. Las credenciales deben existir únicamente en el ambiente que las utiliza.

## 3. Credencial de Firebase

El backend lee `firebase.serviceAccount` de `dazma/prod/firebase` en Secrets Manager. Para producción:

1. Guardar la cuenta de servicio correspondiente al proyecto de Firebase en `firebase.serviceAccount`.
2. Confirmar que la aplicación puede leer el secreto al iniciar.


## 4. Prueba completa después del despliegue

1. Obtener un token vigente de Firebase para un usuario que ya tenga perfil en DAZMA.
2. Solicitar una autorización con `POST /publications/api/v1/photos/upload-authorizations`.
3. Cargar el archivo con `PUT` a la dirección `uploadUrl`, usando exactamente el mismo `Content-Type` informado al pedir la autorización.
4. Conservar el `photoId` recibido.
5. Crear la publicación con `POST /publications/api/v1/publications`, enviando ese identificador dentro de `photoUploadIds`.
6. Confirmar una respuesta `201 Created`, el encabezado `Location` y un registro de publicación con su fotografía en PostgreSQL.
7. Repetir la creación con un `photoId` inexistente y comprobar que el backend responda `409 Conflict` y no guarde la publicación.

## 5. Criterio de finalización

La integración queda validada cuando un usuario autenticado puede obtener la autorización, cargar una foto, crear la publicación y recibir `201`, mientras que una foto inexistente o perteneciente a otro usuario es rechazada. Las claves no deben aparecer en el repositorio, en imágenes, en logs ni en el contrato OpenAPI.
