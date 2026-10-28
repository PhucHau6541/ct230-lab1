package vn.edu.bookstore.legacy.adapter.soap;

import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ghi log toàn bộ thông điệp SOAP gửi/nhận để sinh viên "đọc thông điệp bằng log".
 * Lưu ý vận hành: ở môi trường thật cần che (mask) dữ liệu nhạy cảm trước khi ghi log.
 */
public class SoapLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger LOG = LoggerFactory.getLogger(SoapLoggingHandler.class);

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
        // TODO(TH1-5): đọc MessageContext.MESSAGE_OUTBOUND_PROPERTY để biết chiều gửi/nhận,
        //   ghi context.getMessage() ra ByteArrayOutputStream (writeTo) rồi LOG.info(...)
        LOG.debug("SoapLoggingHandler chưa được hiện thực (fault={})", fault);
    }

    @Override
    public Set<QName> getHeaders() {
        return Set.of();
    }

    @Override
    public void close(MessageContext context) {
        // không cần giải phóng tài nguyên
    }
}
