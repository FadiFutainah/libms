package com.maids.libms;

import com.jayway.jsonpath.JsonPath;
import com.maids.libms.book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class BookApiControllerTests extends BaseTestUseCases {
    @MockBean
    BookService bookService;
    private String accessToken;
    @Autowired
    private MockMvc mockMvc;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @BeforeEach
    public void setup() throws Exception {
        accessToken = super.authenticate(mockMvc);
    }

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

    @Test
    public void testBorrowBook() throws Exception {
        String bookId = "1";
        String patronId = "1";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/" + bookId + "/patron/" + patronId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testReturnBook() throws Exception {
        String bookId = "1";
        String patronId = "1";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/return/" + bookId + "/patron/" + patronId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetBooks() throws Exception {
        int page = 0;
        int size = 10;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
