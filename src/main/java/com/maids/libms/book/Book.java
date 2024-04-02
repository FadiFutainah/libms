package com.maids.libms.book;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.maids.libms.author.Author;
import com.maids.libms.main.BaseEntity;
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
public class Book extends BaseEntity<Integer> {
    @NotNull
    @Column(nullable = false)
    String title;

    @NotNull
    @ManyToOne @JoinColumn(name = "author_id")
    @JsonIgnoreProperties(value = "books")
    Author author;

    @NotNull
    @Column(name = "author_id", insertable = false, updatable = false)
    @JsonIgnore
    Integer authorId;

    int publicationYear;

    @NotNull
    @Column(nullable = false, unique = true)
    String isbn;

    int numOfPages;
}
