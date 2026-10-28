package vn.edu.bookstore.legacy.adapter.config;

import java.net.URL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogPortType;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogService;

@Configuration
public class LegacySoapClientConfig {

    /** Thuộc tính timeout của Metro (Jakarta XML WS RI), đơn vị mili giây. */
    static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";

    @Bean
    BookCatalogPortType bookCatalogPort(LegacySoapProperties props) {
        URL wsdl = LegacySoapClientConfig.class.getResource("/wsdl/BookCatalogService.wsdl");
        BookCatalogPortType port = new BookCatalogService(wsdl).getBookCatalogPort();
        // TODO(TH1-4): ép kiểu port sang BindingProvider, đặt vào request context:
        //   - ENDPOINT_ADDRESS_PROPERTY = props.endpoint()
        //   - CONNECT_TIMEOUT / REQUEST_TIMEOUT (mili giây) từ props
        // TODO(TH1-5): nếu props.logMessages() thì thêm SoapLoggingHandler vào handler chain
        return port;
    }
}
