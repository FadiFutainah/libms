package com.maids.libms.patron;

import com.maids.libms.main.CrudService;
import org.springframework.stereotype.Service;

@Service
public class PatronService extends CrudService<Patron, Integer> {
    public PatronService(PatronRepository jpaRepository) {
        super(jpaRepository, "patron");
    }
}
