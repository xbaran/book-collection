package com.slido;

import com.slido.book.BookEntity;
import com.slido.book.BookRepository;
import com.slido.book.author.AuthorEntity;
import com.slido.book.author.AuthorRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.PostConstruct;

@Configuration
@ActiveProfiles(profiles = "default,h2")
public class InitializationConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;

    @PostConstruct
    public void initializeDB() {
        bookRepository.save(
                BookEntity.builder().title("title1").description("desc1").build()
                        .addAuthor(AuthorEntity.builder().name("author1").build())
        );
        bookRepository.save(
                BookEntity.builder().title("title2").description("desc2").build()
                        .addAuthor(AuthorEntity.builder().name("author2").build())
                        .addAuthor(AuthorEntity.builder().name("author3").build())
        );
        bookRepository.save(
                BookEntity.builder().title("title3").description("desc3").build()
                        .addAuthor(AuthorEntity.builder().name("author4").build())
                        .addAuthor(AuthorEntity.builder().name("author5").build())
        );

        bookRepository.save(
                BookEntity.builder().title("title4").description("desc4").build()
                        .addAuthor(AuthorEntity.builder().name("author6").build())
                        .addAuthor(AuthorEntity.builder().name("author7").build())
        );

        bookRepository.save(
                BookEntity.builder().title("title5").description("desc5").build()
                        .addAuthor(AuthorEntity.builder().name("author8").build())
        );

        bookRepository.save(
                BookEntity.builder().title("title6").description("desc6").build()
                        .addAuthor(AuthorEntity.builder().name("author9").build())
        );

        bookRepository.save(
                BookEntity.builder().title("title7").description("desc7").build()
                        .addAuthor(AuthorEntity.builder().name("author10").build())
                        .addAuthor(AuthorEntity.builder().name("author11").build())
        );

        bookRepository.save(
                BookEntity.builder().title("title8").description("desc8").build()
                        .addAuthor(AuthorEntity.builder().name("author12").build())
        );
        bookRepository.save(
                BookEntity.builder().title("search9").description("search_desc9").build()
                        .addAuthor(AuthorEntity.builder().name("author13").build())
        );
        bookRepository.save(
                BookEntity.builder().title("search10").description("search_desc10").build()
                        .addAuthor(AuthorEntity.builder().name("author14").build())
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
