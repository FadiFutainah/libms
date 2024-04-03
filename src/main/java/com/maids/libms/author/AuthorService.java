package com.maids.libms.author;

import com.maids.libms.main.service.CrudService;
import org.springframework.stereotype.Service;

@Service
public class AuthorService extends CrudService<Author, Integer> {
    public AuthorService(AuthorRepository jpaRepository) {
        super(jpaRepository, "Author");
    }
}
