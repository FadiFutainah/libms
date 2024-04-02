package com.maids.libms.patron;

import com.maids.libms.contact.Contact;
import com.maids.libms.main.CrudService;
import com.maids.libms.main.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PatronService extends CrudService<Patron, Integer> {
    public PatronService(PatronRepository jpaRepository) {
        super(jpaRepository, "patron");
    }

    @Override
    public ResponseEntity<ResponseDto<Patron>> create(Patron resource) {
        for (Contact contact : resource.getContacts()) {
            contact.setPatron(resource);
        }
        return super.create(resource);
    }
}
