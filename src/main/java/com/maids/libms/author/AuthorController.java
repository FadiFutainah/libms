package com.maids.libms.author;

import com.maids.libms.main.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
public class AuthorController extends CrudController<Author, Integer> {
    public AuthorController(AuthorService crudService) {
        super(crudService);
    }
}
