package co.dazma.api.notifications.infrastructure.output.externalservices.resend;

import co.dazma.api.notifications.domain.EmailMessage;
import co.dazma.api.notifications.infrastructure.configuration.ResendConfiguration;
import co.dazma.api.notifications.infrastructure.configuration.ResendProperties;
import co.dazma.api.shared.infrastructure.observability.ExternalDependencyObservation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@EnabledIfEnvironmentVariable(named = "RESEND_API_KEY", matches = ".+")
@EnabledIfEnvironmentVariable(named = "DAZMA_TEST_EMAIL", matches = ".+")
class ResendNotificationGatewayManualTest {

    private static final String SENDER = "DAZMA <no-reply@notificaciones.dazma.store>";

    @Test
    void shouldDeliverARealEmailThroughResend() {
        ResendProperties properties = new ResendProperties(
                System.getenv("RESEND_API_KEY"),
                SENDER,
                Duration.ofSeconds(5),
                Duration.ofSeconds(10)
        );
        ResendNotificationGateway gateway = new ResendNotificationGateway(
                new ResendConfiguration().resend(properties),
                properties,
                new ExternalDependencyObservation(ObservationRegistry.NOOP)
        );
        EmailMessage message = new EmailMessage(
                System.getenv("DAZMA_TEST_EMAIL"),
                "Tu publicación ya está en DAZMA",
                publicationCreatedHtml("Bicicleta de montaña Trek Marlin 5", "Rionegro", "1.250.000"),
                "manual-test/" + UUID.randomUUID()
        );

        assertDoesNotThrow(() -> gateway.send(message));
    }

    private static String publicationCreatedHtml(String name, String municipality, String price) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background-color:#f2f4f7;font-family:Arial,Helvetica,sans-serif;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f2f4f7;padding:32px 16px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:560px;background-color:#ffffff;border-radius:12px;overflow:hidden;">
                          <tr>
                            <td style="background-color:#0f766e;padding:24px 32px;">
                              <span style="color:#ffffff;font-size:24px;font-weight:bold;letter-spacing:2px;">DAZMA</span>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px;">
                              <h1 style="margin:0 0 12px;font-size:22px;color:#111827;">¡Tu publicación ya está disponible!</h1>
                              <p style="margin:0 0 24px;font-size:15px;line-height:1.6;color:#4b5563;">
                                Los usuarios de tu zona ya pueden verla y enviarte propuestas de compra o intercambio.
                              </p>
                              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f9fafb;border:1px solid #e5e7eb;border-radius:8px;">
                                <tr>
                                  <td style="padding:20px;">
                                    <p style="margin:0 0 8px;font-size:17px;font-weight:bold;color:#111827;">%s</p>
                                    <p style="margin:0 0 4px;font-size:14px;color:#6b7280;">%s</p>
                                    <p style="margin:0;font-size:18px;font-weight:bold;color:#0f766e;">$ %s COP</p>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:20px 32px;background-color:#f9fafb;border-top:1px solid #e5e7eb;">
                              <p style="margin:0;font-size:12px;line-height:1.5;color:#9ca3af;">
                                Recibiste este correo porque creaste una publicación en DAZMA. Este es un mensaje automático, no respondas a esta dirección.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(name, municipality, price);
    }
}
