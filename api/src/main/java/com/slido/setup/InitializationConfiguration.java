package com.slido.setup;

import com.google.common.collect.Lists;
import com.slido.book.BookEntity;
import com.slido.book.BookRepository;
import com.slido.book.author.AuthorEntity;
import com.slido.book.author.AuthorRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Profile("init")
public class InitializationConfiguration implements ApplicationContextAware {

    private final Logger l = LoggerFactory.getLogger(InitializationConfiguration.class);

    private ApplicationContext applicationContext;

    @Value("classpath:data/book.csv")
    Resource bookCsvFile;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;

    @PostConstruct
    public void initializeDB() throws IOException {
        l.info("Loading sample data...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(bookCsvFile.getInputStream()));
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);

        Function<CSVRecord, String> title = r ->
                Optional.of(r.get(1)).orElse("Unknown").replaceAll("\\p{C}", "?");
        Function<CSVRecord, String> desc =  r ->
                Optional.of(r.get(19)).orElse("Unknown").replaceAll("\\p{C}", "?");
        Function<CSVRecord, List<String>> authorNames = r ->
                Optional.of(r.get(5))
                .map($ -> Arrays.asList($.trim().replaceAll("\\p{C}", "?").split(",")))
                .orElse(Lists.newArrayList());

        parser.getRecords().stream().skip(1).forEach(rec -> {
            bookRepository.save(
                    BookEntity.builder()
                            .title(title.apply(rec))
                            .description(desc.apply(rec))
                            .build()
                            .addAuthors(
                                    authorNames.apply(rec).stream()
                                            .map(authorName -> AuthorEntity.builder().name(authorName).build())
                                            .collect(Collectors.toList())
                            )
            );
        });
        l.info("Sample data loaded.");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
