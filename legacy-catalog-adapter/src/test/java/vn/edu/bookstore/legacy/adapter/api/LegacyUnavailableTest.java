
package vn.edu.bookstore.legacy.adapter.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.bookstore.legacy.adapter.LegacySoapTestServer;

@SpringBootTest(properties = "bookstore.legacy.read-timeout=500ms")
@AutoConfigureMockMvc
class LegacyUnavailableTest {

    static final LegacySoapTestServer SLOW =
            LegacySoapTestServer.start(2000);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("bookstore.legacy.endpoint", SLOW::url);
    }

    @AfterAll
    static void stop() {
        SLOW.close();
    }

    @Autowired
    MockMvc mvc;

    @Test
    void heThongKeThuaChamThanh504() throws Exception {
        mvc.perform(get("/api/v1/legacy/books/9780134685991"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.type")
                        .value("https://api.bookstore.vn/problems/upstream-unavailable"));
    }
}

