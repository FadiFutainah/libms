package com.maids.libms.patron;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.maids.libms.borrowing.record.BorrowingRecord;
import com.maids.libms.contact.Contact;
import com.maids.libms.main.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Builder @Accessors(chain = true)
@NoArgsConstructor @AllArgsConstructor
public class Patron extends BaseEntity<Integer> {
    @NotNull
    @Column(nullable = false)
    String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "patron", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "patron")
    Set<Contact> contacts = new HashSet<>();


    @OneToMany(fetch =  FetchType.EAGER, mappedBy = "patron")
    @JsonIgnoreProperties(value = "patron")
    Set<BorrowingRecord> borrowingRecords = new HashSet<>();
}
