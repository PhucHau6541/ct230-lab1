package vn.edu.bookstore.legacy.adapter.soap;

import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.List;
import org.springframework.stereotype.Component;
import vn.edu.bookstore.legacy.adapter.soap.generated.Book;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogFault;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogPortType;

/**
 * Tầng chống ăn mòn (anti-corruption layer): gọi BookCatalogService và chuyển
 * kiểu dữ liệu + lỗi SOAP sang mô hình nội bộ {@link LegacyBook} / {@link LegacyCatalogException}.
 */
@Component
public class LegacyCatalogClient {

    private final BookCatalogPortType port;

    public LegacyCatalogClient(BookCatalogPortType port) {
        this.port = port;
    }

    public LegacyBook getBook(String isbn) {
        // TODO(TH1-6): tạo GetBookRequest, gọi port.getBook(request), ánh xạ bằng toModel(...)
        // TODO(TH1-7): bắt BookCatalogFault, SOAPFaultException, WebServiceException
        //              và ném lại bằng translate(...) / unavailable(...)
        throw new UnsupportedOperationException("TODO(TH1-6)");
    }

    public List<LegacyBook> search(String keyword, int maxResults) {
        // TODO(TH1-6): tạo SearchBooksRequest (keyword, maxResults), gọi port.searchBooks(...)
        throw new UnsupportedOperationException("TODO(TH1-6)");
    }

    /** TODO(TH1-7): BOOK_NOT_FOUND → NOT_FOUND; INVALID_ISBN/INVALID_REQUEST → INVALID_REQUEST; còn lại → UPSTREAM_ERROR. */
    static LegacyCatalogException translate(BookCatalogFault fault) {
        throw new UnsupportedOperationException("TODO(TH1-7)");
    }

    /** TODO(TH1-7): faultcode Client → INVALID_REQUEST, Server → UPSTREAM_ERROR. */
    static LegacyCatalogException translate(SOAPFaultException e) {
        throw new UnsupportedOperationException("TODO(TH1-7)");
    }

    /** TODO(TH1-7): phân biệt hết thời gian chờ (SocketTimeoutException) với không kết nối được. */
    static LegacyCatalogException unavailable(WebServiceException e) {
        throw new UnsupportedOperationException("TODO(TH1-7)");
    }

    /** Ánh xạ kiểu SOAP → mô hình nội bộ (không để lớp sinh từ WSDL "rò" ra API REST). */
    static LegacyBook toModel(Book b) {
        Integer authorId = b.getAuthorId() == null ? null : b.getAuthorId().intValue();
        Integer year = b.getPublishedYear() == null ? null : b.getPublishedYear().getYear();
        return new LegacyBook(b.getIsbn(), b.getTitle(), b.getAuthor(), authorId, year,
                b.getPrice().getAmount(), b.getPrice().getCurrency().value());
    }
}
