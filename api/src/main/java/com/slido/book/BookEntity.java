package com.slido.book;

import com.google.common.base.Preconditions;
import com.slido.book.author.AuthorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Builder
@Entity(name = "book")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BookEntity {
    @Id @GeneratedValue
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Column(length = 255)
    @Size(max = 255, message = "Title max length is 255 characters")
    private String title;

    @Size(max = 65000, message = "Title max length is 65000 characters")
    @Column(length = 65000)
    private String description;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = AuthorEntity.class)
    @Size(min = 1, message = "At least one author has to be provided")
    @Builder.Default
    private List<AuthorEntity> authors = new ArrayList<>();

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Version
    private Long sequence;

    public BookEntity addAuthor(AuthorEntity authorEntity) {
        authorEntity.setBook(this);
        this.authors.add(authorEntity);
        return this;
    }

    public BookEntity addAuthors(List<AuthorEntity> authorEntities) {
        Preconditions.checkNotNull(authorEntities);
        authorEntities.forEach(this::addAuthor);
        return this;
    }

    public Optional<AuthorEntity> findAuthor(Long authorId) {
        return this.authors.stream().filter(author -> author.getId().equals(authorId)).findFirst();
    }

    public Optional<AuthorEntity> findAuthor(AuthorEntity authorEntity) {
        return findAuthor(authorEntity.getId());
    }

}
