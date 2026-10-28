package vn.edu.bookstore.legacy.adapter.soap;

import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import org.springframework.stereotype.Component;
import vn.edu.bookstore.legacy.adapter.soap.generated.Book;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogFault;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogPortType;
import vn.edu.bookstore.legacy.adapter.soap.generated.GetBookRequest;
import vn.edu.bookstore.legacy.adapter.soap.generated.SearchBooksRequest;

/**
 * Tầng chống ăn mòn (anti-corruption layer):
 * gọi BookCatalogService và chuyển dữ liệu, lỗi SOAP
 * sang mô hình nội bộ của BookStore.
 */
@Component
public class LegacyCatalogClient {

    private final BookCatalogPortType port;

    public LegacyCatalogClient(BookCatalogPortType port) {
        this.port = port;
    }

    /**
     * TH1-6 và TH1-7: lấy sách theo ISBN từ dịch vụ SOAP.
     */
    public LegacyBook getBook(String isbn) {
        GetBookRequest request = new GetBookRequest();
        request.setIsbn(isbn);

        try {
            Book book = port.getBook(request).getBook();
            return toModel(book);
        } catch (BookCatalogFault fault) {
            throw translate(fault);
        } catch (SOAPFaultException fault) {
            throw translate(fault);
        } catch (WebServiceException e) {
            throw unavailable(e);
        }
    }

    /**
     * TH1-6 và TH1-7: tìm sách bằng dịch vụ SOAP.
     */
    public List<LegacyBook> search(String keyword, int maxResults) {
        SearchBooksRequest request = new SearchBooksRequest();
        request.setKeyword(keyword);
        request.setMaxResults(maxResults);

        try {
            return port.searchBooks(request)
                    .getBook()
                    .stream()
                    .map(LegacyCatalogClient::toModel)
                    .toList();
        } catch (BookCatalogFault fault) {
            throw translate(fault);
        } catch (SOAPFaultException fault) {
            throw translate(fault);
        } catch (WebServiceException e) {
            throw unavailable(e);
        }
    }

    /**
     * TH1-7: dịch lỗi SOAP có ErrorDetail.
     */
    static LegacyCatalogException translate(BookCatalogFault fault) {
        var detail = fault.getFaultInfo();

        String code = detail == null || detail.getCode() == null
                ? "UNKNOWN"
                : detail.getCode();

        String message = detail == null || detail.getMessage() == null
                ? fault.getMessage()
                : detail.getMessage();

        LegacyCatalogException.Kind kind = switch (code) {
            case "BOOK_NOT_FOUND" ->
                    LegacyCatalogException.Kind.NOT_FOUND;

            case "INVALID_ISBN", "INVALID_REQUEST" ->
                    LegacyCatalogException.Kind.INVALID_REQUEST;

            default ->
                    LegacyCatalogException.Kind.UPSTREAM_ERROR;
        };

        return new LegacyCatalogException(
                kind,
                code,
                message,
                fault
        );
    }

    /**
     * TH1-7: dịch SOAP Fault không có ErrorDetail theo faultcode.
     */
    static LegacyCatalogException translate(SOAPFaultException e) {
        var soapFault = e.getFault();

        String faultCode = soapFault == null
                || soapFault.getFaultCodeAsQName() == null
                ? "Server"
                : soapFault.getFaultCodeAsQName().getLocalPart();

        LegacyCatalogException.Kind kind =
                "Client".equals(faultCode)
                        ? LegacyCatalogException.Kind.INVALID_REQUEST
                        : LegacyCatalogException.Kind.UPSTREAM_ERROR;

        return new LegacyCatalogException(
                kind,
                "soap:" + faultCode,
                e.getMessage(),
                e
        );
    }

    /**
     * TH1-7: phân biệt timeout và không kết nối được.
     */
    static LegacyCatalogException unavailable(WebServiceException e) {
        boolean timeout = hasCause(e, SocketTimeoutException.class)
                || hasCause(e, HttpTimeoutException.class);

        if (timeout) {
            return new LegacyCatalogException(
                    LegacyCatalogException.Kind.UPSTREAM_TIMEOUT,
                    null,
                    "Hệ thống kế thừa phản hồi quá thời gian chờ",
                    e
            );
        }

        return new LegacyCatalogException(
                LegacyCatalogException.Kind.UPSTREAM_UNAVAILABLE,
                null,
                "Không thể kết nối đến hệ thống kế thừa",
                e
        );
    }

    private static boolean hasCause(
            Throwable error,
            Class<? extends Throwable> type) {

        for (Throwable current = error;
             current != null;
             current = current.getCause()) {

            if (type.isInstance(current)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Ánh xạ dữ liệu SOAP sang mô hình nội bộ.
     */
    static LegacyBook toModel(Book b) {
        Integer authorId = b.getAuthorId() == null
                ? null
                : b.getAuthorId().intValue();

        Integer year = b.getPublishedYear() == null
                ? null
                : b.getPublishedYear().getYear();

        return new LegacyBook(
                b.getIsbn(),
                b.getTitle(),
                b.getAuthor(),
                authorId,
                year,
                b.getPrice().getAmount(),
                b.getPrice().getCurrency().value()
        );
    }
}
