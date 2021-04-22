package com.slido.book.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    @Query("SELECT a FROM author a WHERE a.book.id = :bookId AND a.id = :authorId")
    AuthorEntity findAuthorByBookIdAndId(
            @Param("bookId") Long bookId,
            @Param("authorId") Long authorId);

}
