package vn.edu.bookstore.legacy.adapter.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.bookstore.legacy.adapter.soap.LegacyCatalogClient;

/** API REST bọc dịch vụ SOAP kế thừa: /api/v1/legacy/books */
@RestController
@RequestMapping("/api/v1/legacy/books")
public class LegacyBookController {

    private final LegacyCatalogClient client;

    public LegacyBookController(LegacyCatalogClient client) {
        this.client = client;
    }

    @GetMapping("/{isbn}")
    public BookView getBook(@PathVariable String isbn) {
        // TODO(TH1-6): kiểm tra isbn khớp ^97[89][0-9]{10}$ (sai → InvalidRequestException),
        //              gọi client.getBook(isbn) rồi BookView.from(...)
        throw new UnsupportedOperationException("TODO(TH1-6)");
    }

    @GetMapping
    public List<BookView> search(@RequestParam("q") String keyword,
                                 @RequestParam(name = "limit", defaultValue = "10") int limit) {
        // TODO(TH1-6): q ít nhất 2 ký tự, limit 1..50; gọi client.search(...)
        throw new UnsupportedOperationException("TODO(TH1-6)");
    }
}
