package com.slido.book.author;

import com.slido.book.BookEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Builder
@Entity(name = "author")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuthorEntity implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Title is mandatory")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Version
    private Long sequence;
}
