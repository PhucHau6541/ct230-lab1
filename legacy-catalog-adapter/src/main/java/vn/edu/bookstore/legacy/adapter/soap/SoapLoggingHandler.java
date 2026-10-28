
package vn.edu.bookstore.legacy.adapter.soap;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ghi log thông điệp SOAP gửi và nhận.
 * Trong môi trường thật, cần che dữ liệu nhạy cảm trước khi ghi log.
 */
public class SoapLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger LOG =
            LoggerFactory.getLogger(SoapLoggingHandler.class);

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        log(context, false);
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        log(context, true);
        return true;
    }

    private void log(SOAPMessageContext context, boolean fault) {
        boolean outbound = Boolean.TRUE.equals(
                context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY));

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            context.getMessage().writeTo(output);

            String direction = outbound ? "OUTBOUND" : "INBOUND";
            String type = fault ? "FAULT" : "MESSAGE";
            String message = output.toString(StandardCharsets.UTF_8);

            LOG.info("SOAP {} {}:\n{}", direction, type, message);

        } catch (SOAPException | IOException e) {
            LOG.warn("Không thể ghi log thông điệp SOAP", e);
        }
    }

    @Override
    public Set<QName> getHeaders() {
        return Set.of();
    }

    @Override
    public void close(MessageContext context) {
        // Không cần giải phóng tài nguyên.
    }
}
