package com.maids.libms;


import com.maids.libms.book.BookService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class LibmsApplicationTests {
    @MockBean
    BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void fetchBooks() {
        try {
            MockHttpServletRequestBuilder requestBuilder = get("/api/books")
                    .param("page", "0")
                    .param("size", "10");
            mockMvc.perform(requestBuilder);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public double measureExecutionTime(Runnable function) {
        long startTime = System.nanoTime();
        function.run();
        long endTime = System.nanoTime();
        long executionTimeNano = endTime - startTime;
        return executionTimeNano / 1_000_000_000.0;
    }

    @Test
    void testCachedBookService() {
        log.info("calling the api...");
        double duration1 = measureExecutionTime(this::fetchBooks);
        double duration2 = measureExecutionTime(this::fetchBooks);
        log.info("The service for the first call takes: " + duration1);
        log.info("The service for the second call takes: " + duration2);
        assertThat(duration1).isGreaterThan(duration2);
    }
}
