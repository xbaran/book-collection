package com.slido.book;

import com.slido.book.api.BooksApiDelegate;
import com.slido.book.author.AuthorRepository;
import com.slido.book.model.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@ActiveProfiles(profiles = "default,h2")
public class BookControllerTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    BooksApiDelegate controller;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void postBook() throws Exception {
        ResponseEntity<Book> resp = controller.postBook(new BookPost().title("test1").description("test1"));
        assertTrue("book not created", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("book title incorrect", Objects.requireNonNull(resp.getBody()).getTitle().equals("test1"));
        assertTrue("book title incorrect", Objects.requireNonNull(resp.getBody()).getDescription().equals("test1"));
    }

    @Test
    public void postBookShouldFailWithEmptyTitle() throws Exception {
        ConstraintViolationException exception = Assert.assertThrows(
                "book post has no error message",
                ConstraintViolationException.class,
                () -> controller.postBook(new BookPost().description("test1"))
        );
        assertTrue("book post has wrong error message count", exception.getConstraintViolations() != null && exception.getConstraintViolations().size() == 1);
    }

    @Test
    public void getBook() throws Exception {
        ResponseEntity<Book> resp = controller.getBook(1L);
        assertTrue("book not found", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("book title incorrect", resp.getBody().getTitle().equals("title1"));
        assertTrue("book description incorrect", resp.getBody().getDescription().equals("desc1"));
        assertTrue("book authors size incorrect", resp.getBody().getAuthors().size() == 1);
        assertTrue("book author name incorrect", resp.getBody().getAuthors().get(0).getName().equals("author1"));
    }

    @Test
    public void patchBookTitle() throws Exception {
        ResponseEntity<Book> resp = controller.patchBook(2L, new BookPatch().title("patched2"));
        assertTrue("book not patched", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("book title incorrect", resp.getBody().getTitle().equals("patched2"));
        assertTrue("book description incorrect", resp.getBody().getDescription().equals("desc2"));
        assertTrue("book authors size incorrect", resp.getBody().getAuthors().size() == 2);
    }

    @Test
    public void patchBookDescription() throws Exception {
        ResponseEntity<Book> resp = controller.patchBook(3L, new BookPatch().description("patched3"));
        assertTrue("book not patched", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("book title incorrect", resp.getBody().getTitle().equals("title3"));
        assertTrue("book description incorrect", resp.getBody().getDescription().equals("patched3"));
        assertTrue("book authors size incorrect", resp.getBody().getAuthors().size() == 2);
    }

    @Test
    @Ignore
    //TODO: check problem
    public void patchAuthors() throws Exception {
        ResponseEntity<Book> resp = controller.patchBook(4L, new BookPatch().authors(Lists.newArrayList(
                    new AuthorPatch().name("added8"),new AuthorPatch().id(6L).name("patched6")
                ))
        );
        assertTrue("book not patched", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("book title incorrect", resp.getBody().getTitle().equals("title4"));
        assertTrue("book description incorrect", resp.getBody().getDescription().equals("desc4"));
        assertTrue("book authors size incorrect", resp.getBody().getAuthors().size() == 3);
        assertTrue("book authors missing added author", resp.getBody().getAuthors().stream().anyMatch($ -> $.getName().equals("added8")));
        assertTrue("book authors missing patched author", resp.getBody().getAuthors().stream().anyMatch($ -> $.getName().equals("patched6")));
    }

    @Test
    public void deleteBook() throws Exception {
        ResponseEntity<Void> resp = controller.deleteBook(5L);
        assertTrue("book not deleted", resp.getStatusCode() == HttpStatus.OK);
        assertFalse("book not deleted in database", bookRepository.existsById(5L));
        assertFalse("author not deleted in database", authorRepository.existsById(8L));
    }

    @Test
    public void searchEqual() throws Exception {
        ResponseEntity<BookSearch> resp = controller.getBooks(Optional.of("title==search9"), Optional.of(0), Optional.empty(), Optional.empty());
        assertTrue("book not found", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("book title incorrect", Objects.requireNonNull(resp.getBody()).getBooks().get(0).getTitle().equals("search9"));
    }

    @Test
    public void searchLike() throws Exception {
        ResponseEntity<BookSearch> resp = controller.getBooks(Optional.of("title==search*"), Optional.of(0), Optional.empty(), Optional.empty());
        assertTrue("books not found", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("books count incorrect", resp.getBody().getBooks().size() == 2);
    }

    @Test
    public void searchTooMuchBooks() throws Exception {
        ResponseStatusException ex = Assert.assertThrows(
                "there is too much books",
                ResponseStatusException.class,
                () -> controller.getBooks(Optional.of("title==search*"), Optional.of(0), Optional.of(1000), Optional.empty())
        );
        assertTrue("wrong http status",ex.getStatus() == HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @Test
    @Ignore
    //TODO: check problem
    public void searchNoContent() throws Exception {
        ResponseStatusException ex = Assert.assertThrows(
                "there is too much books",
                ResponseStatusException.class,
                () -> controller.getBooks(Optional.of("title==missing_title"), Optional.of(0), Optional.of(1000), Optional.empty())
        );
        assertTrue("wrong http status",ex.getStatus() == HttpStatus.NO_CONTENT);
    }
    

    @Test
    public void getAuthor() throws Exception {
        ResponseEntity<Author> resp = controller.getBookAuthor(1L, 1L);
        assertTrue("author not found", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("author title incorrect", resp.getBody().getName().equals("author1"));
        //missing book reference?
    }

    @Test
    public void patchAuthorName() throws Exception {
        ResponseEntity<Author> resp = controller.patchBookAuthor(9L, 6L, new AuthorPost().name("patched9"));
        assertTrue("author not patched", resp.getStatusCode() == HttpStatus.OK);
        assertTrue("author id incorrect", resp.getBody().getId().equals(9L));
        assertTrue("author name incorrect", resp.getBody().getName().equals("patched9"));
    }

    @Test
    public void deleteAuthor() throws Exception {
        ResponseEntity<Void> resp = controller.deleteBookAuthor(10L, 7L);
        assertTrue("author not deleted", resp.getStatusCode() == HttpStatus.OK);
        assertFalse("author not deleted in database", authorRepository.existsById(10L));
    }

    @Test
    public void deleteLastAuthor() throws Exception {
        ResponseEntity<Void> resp = controller.deleteBookAuthor(12L, 8L);
        assertTrue("author not deleted", resp.getStatusCode() == HttpStatus.OK);
        assertFalse("author not deleted in database", authorRepository.existsById(12L));
        assertFalse("book not deleted in database", bookRepository.existsById(8L));
    }


}