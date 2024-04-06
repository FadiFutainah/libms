package com.maids.libms;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorApiControllerTests extends BaseTestUseCases {
    @MockBean
    BookService bookService;
    @Autowired
    private MockMvc mockMvc;
    private String accessToken;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    public void setup() throws Exception {
        accessToken = super.authenticate(mockMvc);
    }

    @Test
    public void testCreateAuthor() throws Exception {
        String requestBody = "{\"name\": \"John Doe\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
