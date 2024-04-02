package com.maids.libms.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.maids.libms.main.BaseEntity;
import com.maids.libms.patron.Patron;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Getter @Setter
@Builder @Accessors(chain = true)
@NoArgsConstructor @AllArgsConstructor
public class Contact extends BaseEntity<Integer> {
    @NotNull
    @Column(nullable = false)
    String name;

    @NotNull
    @Column(nullable = false)
    String url;

    @NotNull
    @Column(nullable = false)
    @JsonIgnoreProperties(value = "contacts")
    @ManyToOne @JoinColumn(name = "patron_id")
    Patron patron;
}
