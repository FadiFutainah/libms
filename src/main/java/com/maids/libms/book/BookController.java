package com.maids.libms.book;

import com.maids.libms.main.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController extends CrudController<Book, Integer> {
    public BookController(BookService bookService) {
        super(bookService);
    }
}
