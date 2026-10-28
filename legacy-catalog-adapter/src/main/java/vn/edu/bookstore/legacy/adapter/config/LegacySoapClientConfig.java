package vn.edu.bookstore.legacy.adapter.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.edu.bookstore.legacy.adapter.soap.SoapLoggingHandler;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogPortType;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogService;

@Configuration
public class LegacySoapClientConfig {

    /** Thuộc tính timeout của Metro (Jakarta XML WS RI), đơn vị mili giây. */
    static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";

    @Bean
    BookCatalogPortType bookCatalogPort(LegacySoapProperties props) {
        URL wsdl = LegacySoapClientConfig.class.getResource(
                "/wsdl/BookCatalogService.wsdl");

        BookCatalogPortType port =
                new BookCatalogService(wsdl).getBookCatalogPort();

        // TH1-4: Cấu hình địa chỉ và thời gian chờ SOAP.
        BindingProvider bp = (BindingProvider) port;

        bp.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                props.endpoint());

        bp.getRequestContext().put(
                CONNECT_TIMEOUT,
                Math.toIntExact(props.connectTimeout().toMillis()));

        bp.getRequestContext().put(
                REQUEST_TIMEOUT,
                Math.toIntExact(props.readTimeout().toMillis()));

        // TH1-5: Ghi log thông điệp SOAP nếu được bật.
        if (props.logMessages()) {
            List<Handler> chain =
                    new ArrayList<>(bp.getBinding().getHandlerChain());

            chain.add(new SoapLoggingHandler());

            bp.getBinding().setHandlerChain(chain);
        }

        return port;
    }
}
