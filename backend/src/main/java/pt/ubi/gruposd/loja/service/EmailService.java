package pt.ubi.gruposd.loja.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pt.ubi.gruposd.loja.dto.SaleResponse;

// Envia o email HTML de confirmação de encomenda após o checkout.
// O envio é assíncrono (@Async) para não atrasar a resposta ao cliente. Pode ser desativado com app.mail.enabled=false.
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Envia o email de confirmação. Se mail.enabled=false ou houver erro SMTP, regista em log sem interromper a venda.
    @Async
    public void sendOrderConfirmation(String toEmail, String customerName, SaleResponse sale) {
        if (!mailEnabled) {
            log.debug("Email desactivado — confirmação de encomenda #{} não enviada", sale.id());
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Encomenda confirmada #" + sale.id() + " — SportFlow");
            helper.setText(buildHtml(customerName, sale), true);

            mailSender.send(message);
            log.info("Email de confirmação enviado para {}", toEmail);
        } catch (MessagingException e) {
            log.warn("Falha ao enviar email de confirmação para {}: {}", toEmail, e.getMessage());
        }
    }

    // Gera o HTML do email com tabela de produtos, totais, morada e pagamento. Estilos inline porque os clientes de email ignoram CSS externo.
    private String buildHtml(String customerName, SaleResponse sale) {
        StringBuilder items = new StringBuilder();
        for (var item : sale.items()) {
            items.append("""
                <tr>
                  <td style="padding:8px 12px;border-bottom:1px solid #e2e4e9">%s</td>
                  <td style="padding:8px 12px;border-bottom:1px solid #e2e4e9;text-align:center">%d</td>
                  <td style="padding:8px 12px;border-bottom:1px solid #e2e4e9;text-align:right">%.2f €</td>
                </tr>
                """.formatted(item.productName(), item.quantity(), item.subtotal()));
        }

        // Traduz os códigos internos dos métodos de pagamento para etiquetas em português
        // para o email ser mais legível para o utilizador final.
        String paymentLabel = switch (sale.paymentMethod() == null ? "" : sale.paymentMethod()) {
            case "CARD"       -> "Cartão de crédito/débito";
            case "MBWAY"      -> "MB Way";
            case "MULTIBANCO" -> "Referência Multibanco";
            case "COD"        -> "Pagamento na entrega";
            default           -> sale.paymentMethod();
        };

        String address = sale.shippingAddress()
            + (sale.shippingAddress2() != null && !sale.shippingAddress2().isBlank()
                ? ", " + sale.shippingAddress2() : "")
            + "<br>" + sale.shippingPostalCode() + " " + sale.shippingCity()
            + (sale.shippingRegion() != null && !sale.shippingRegion().isBlank()
                ? ", " + sale.shippingRegion() : "")
            + "<br>" + sale.shippingCountry();

        return """
            <!DOCTYPE html>
            <html lang="pt">
            <head><meta charset="UTF-8"></head>
            <body style="margin:0;padding:0;background:#f4f5f7;font-family:Inter,Arial,sans-serif">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding:32px 16px">
                  <table width="600" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 16px rgba(0,0,0,.10)">

                    <!-- Header -->
                    <tr><td style="background:#003ec7;padding:28px 40px">
                      <span style="font-size:24px;font-weight:900;color:#fff;letter-spacing:-.02em">
                        Sport<span style="color:#fe6b00">Flow</span>
                      </span>
                    </td></tr>

                    <!-- Body -->
                    <tr><td style="padding:36px 40px">
                      <h1 style="margin:0 0 8px;font-size:22px;color:#1a1a2e">Encomenda confirmada! ✓</h1>
                      <p style="margin:0 0 24px;color:#6b7280">Olá, %s. A tua encomenda foi recebida e está a ser processada.</p>

                      <p style="margin:0 0 4px;font-size:13px;font-weight:600;color:#6b7280;text-transform:uppercase;letter-spacing:.05em">Referência</p>
                      <p style="margin:0 0 24px;font-size:18px;font-weight:700;color:#003ec7">#%d — %s</p>

                      <!-- Produtos -->
                      <table width="100%%" cellpadding="0" cellspacing="0" style="border:1px solid #e2e4e9;border-radius:8px;overflow:hidden;margin-bottom:24px">
                        <tr style="background:#f4f5f7">
                          <th style="padding:10px 12px;text-align:left;font-size:12px;color:#6b7280;font-weight:600">PRODUTO</th>
                          <th style="padding:10px 12px;text-align:center;font-size:12px;color:#6b7280;font-weight:600">QTD</th>
                          <th style="padding:10px 12px;text-align:right;font-size:12px;color:#6b7280;font-weight:600">SUBTOTAL</th>
                        </tr>
                        %s
                        <tr style="background:#f4f5f7">
                          <td colspan="2" style="padding:12px;font-weight:700">Total</td>
                          <td style="padding:12px;text-align:right;font-weight:700;color:#003ec7;font-size:16px">%.2f €</td>
                        </tr>
                      </table>

                      <!-- Morada e Pagamento -->
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:24px">
                        <tr>
                          <td width="50%%" style="vertical-align:top;padding-right:16px">
                            <p style="margin:0 0 6px;font-size:13px;font-weight:600;color:#6b7280;text-transform:uppercase;letter-spacing:.05em">Morada de entrega</p>
                            <p style="margin:0;font-size:14px;color:#1a1a2e;line-height:1.6">%s<br>%s</p>
                          </td>
                          <td width="50%%" style="vertical-align:top">
                            <p style="margin:0 0 6px;font-size:13px;font-weight:600;color:#6b7280;text-transform:uppercase;letter-spacing:.05em">Método de pagamento</p>
                            <p style="margin:0;font-size:14px;color:#1a1a2e">%s</p>
                          </td>
                        </tr>
                      </table>

                      <a href="http://localhost:3000/orders" style="display:inline-block;background:#003ec7;color:#fff;padding:12px 24px;border-radius:6px;font-weight:700;font-size:14px;text-decoration:none">
                        Ver as minhas encomendas
                      </a>
                    </td></tr>

                    <!-- Footer -->
                    <tr><td style="background:#f4f5f7;padding:20px 40px;text-align:center;font-size:12px;color:#9ca3af">
                      SportFlow © 2025 — Loja Desportiva Online
                    </td></tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(
                customerName,
                sale.id(),
                sale.createdAt() != null ? sale.createdAt().format(DATE_FMT) : "",
                items.toString(),
                sale.total(),
                sale.shippingName(),
                address,
                paymentLabel
            );
    }
}
