package com.maids.libms.author;

import com.maids.libms.main.CrudService;
import org.springframework.stereotype.Service;

@Service
public class AuthorService extends CrudService<Author, Integer> {
    public AuthorService(AuthorRepository jpaRepository) {
        super(jpaRepository, "Author");
    }
}
