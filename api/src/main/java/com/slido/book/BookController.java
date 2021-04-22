package com.slido.book;

import com.github.tennaito.rsql.jpa.JpaPredicateVisitor;
import com.slido.book.api.BooksApiDelegate;
import com.slido.book.author.AuthorEntity;
import com.slido.book.author.AuthorRepository;
import com.slido.book.model.*;
import com.slido.validation.ValidationService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookController implements BooksApiDelegate {

    private final ModelMapper modelMapper;
    private final ModelMapper patchModelMapper;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final EntityManager entityManager;
    private final ValidationService validator;

    @Value("${slido.book.max.limit:100}")
    private int BOOK_MAX_LIMIT;
    @Value("${slido.book.default.limit:10}")
    private int BOOK_DEFAUL_LIMIT;

    @Autowired
    public BookController(@Qualifier("default") ModelMapper modelMapper,
                          @Qualifier("patch") ModelMapper patchModelMapper,
                          BookRepository bookRepository,
                          AuthorRepository authorRepository,
                          EntityManager entityManager,
                          ValidationService validator) {
        this.modelMapper = modelMapper;
        this.patchModelMapper = patchModelMapper;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.entityManager = entityManager;
        this.validator = validator;
    }

    @Override
    public ResponseEntity<Book> postBook(BookPost book) throws Exception {
        BookEntity bookEntity = modelMapper.map(book, BookEntity.class);
        validator.validate(bookEntity);
        BookEntity storedBookEntity = bookRepository.save(bookEntity);
        return ResponseEntity.ok(modelMapper.map(storedBookEntity, Book.class));
    }

    @Override
    public ResponseEntity<Book> getBook(Long bookId) throws Exception {
        BookEntity storedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(modelMapper.map(storedBook, Book.class));
    }

    @Override
    public ResponseEntity<Book> patchBook(Long bookId, BookPatch book) throws Exception {
        BookEntity bookToPatch = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        patchModelMapper.map(book, bookToPatch);
        validator.validate(bookToPatch);
        BookEntity savedBook = bookRepository.save(bookToPatch);
        return ResponseEntity.ok(modelMapper.map(savedBook, Book.class));
    }

    @Override
    public ResponseEntity<Book> upsertBook(Long bookId, BookPost book) throws Exception {
        BookEntity bookToUpsert = bookRepository.findById(bookId)
                .orElse(new BookEntity());
        modelMapper.map(book, bookToUpsert);
        validator.validate(bookToUpsert);
        BookEntity savedBook = bookRepository.save(bookToUpsert);
        return ResponseEntity.ok(modelMapper.map(savedBook, Book.class));
    }

    @Override
    public ResponseEntity<Void> deleteBook(Long bookId) throws Exception {
        BookEntity storedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookRepository.delete(storedBook);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Author>> getBookAuthors(Long bookId) throws Exception {
        BookEntity storedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Collection<AuthorEntity> authors = storedBook.getAuthors();
        return ResponseEntity.ok(modelMapper.map(authors, new TypeToken<Collection<Author>>() {
        }.getType()));
    }

    @Override
    public ResponseEntity<Void> deleteBookAuthor(Long authorId, Long bookId) throws Exception {
        BookEntity storedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AuthorEntity authorEntity = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(storedBook.getAuthors().size() > 1) {
            storedBook.getAuthors().remove(authorEntity);
            bookRepository.save(storedBook);
        } else {
            bookRepository.delete(storedBook);
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Author> patchBookAuthor(Long authorId, Long bookId, AuthorPost author) throws Exception {
        BookEntity storedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AuthorEntity authorToPatch = storedBook.findAuthor(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        patchModelMapper.map(author, authorToPatch);
        validator.validate(authorToPatch);
        AuthorEntity savedAuthor = authorRepository.save(authorToPatch);
        return ResponseEntity.ok(modelMapper.map(savedAuthor, Author.class));
    }

    @Override
    public ResponseEntity<Author> getBookAuthor(Long authorId, Long bookId) throws Exception {
        BookEntity storedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AuthorEntity storedAuthor = storedBook.findAuthor(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(modelMapper.map(storedAuthor, Author.class));
    }

    @Override
    public ResponseEntity<Author> upsertBookAuthor(Long authorId, Long bookId, AuthorPost author) throws Exception {
        BookEntity storedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AuthorEntity authorToUpsert = storedBook.findAuthor(authorId)
                .orElse(AuthorEntity.builder().book(storedBook).build());
        modelMapper.map(author, authorToUpsert);
        validator.validate(authorToUpsert);
        AuthorEntity savedAuthor = authorRepository.save(authorToUpsert);
        return ResponseEntity.ok(modelMapper.map(savedAuthor, Author.class));
    }

    @Override
    public ResponseEntity<BookSearch> getBooks(Optional<String> search, Optional<Integer> page, Optional<Integer> limit, Optional<String> sort) throws Exception {
        if (limit.isPresent() && limit.get() > BOOK_MAX_LIMIT) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE);
        }
        Integer reguestLimit = limit.orElse(BOOK_DEFAUL_LIMIT);

        Pageable pageable = PageRequest.of(
                page.orElse(0),
                reguestLimit,
                sort.map(this::buildSortable).orElse(Sort.unsorted())
        );

        Page<BookEntity> booksFound = search.isPresent() ?
                searchBooks(search.get(), pageable) : bookRepository.findAll(pageable);

        return ResponseEntity.ok(new BookSearch()
                .bookTotalCount(booksFound.getTotalElements())
                .booksPerPage(reguestLimit)
                .books(booksFound.map(bookEntity -> modelMapper.map(bookEntity, Book.class)).toList())
        );
    }

    private Page<BookEntity> searchBooks(String search, Pageable pageable) throws Exception {
        Node searchNode = new RSQLParser().parse(search);

        return bookRepository.findAll(
                (root, query, criteriaBuilder) -> {
                    RSQLVisitor<Predicate, EntityManager> visitor =
                            new JpaPredicateVisitor<BookEntity>().defineRoot(root);
                    return searchNode.accept(visitor, entityManager);
                },
                pageable
        );
    }

    private Sort buildSortable(String sort) {
        List<String> sortList = Arrays.asList(sort.trim().toLowerCase().split(","));
        return sortList.stream()
                .filter($ -> !$.isEmpty())
                .reduce(
                        Sort.unsorted(),
                        (orders, s) -> {
                            String[] splits = s.concat(":d").split(":");
                            return orders.and(buildSort(splits[0], Optional.of(splits[1])));
                        },
                        Sort::and
                );
    }

    private static final Sort buildSort(String field, Optional<String> order) {
        return order.filter(o -> o.startsWith("a"))
                .map(o -> Sort.by(Sort.Direction.ASC, field))
                .orElse(Sort.by(Sort.Direction.DESC, field));
    }
}
